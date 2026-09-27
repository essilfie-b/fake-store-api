package com.supply.chain.inventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supply.chain.inventory.dtos.CreateLocationRequest;
import com.supply.chain.inventory.dtos.LocationDto;
import com.supply.chain.inventory.exceptions.ResourceNotFoundException;
import com.supply.chain.inventory.services.LocationService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
@Import(LocationControllerTest.TestSecurityConfig.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private LocationDto locationDto;
    private CreateLocationRequest createLocationRequest;

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

        @Bean
        public TestExceptionHandler testExceptionHandler() {
            return new TestExceptionHandler();
        }
    }

    @RestControllerAdvice
    static class TestExceptionHandler {

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Cannot delete location with existing data"));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
            return ResponseEntity.status(ex.getStatus())
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @BeforeEach
    void setUp() {
        locationDto = new LocationDto(
                1L,
                "Warehouse A",
                "123 Main Street, City, State 12345",
                "Test Controller",
                "test.email@location.com",
                "+1-555-123-4567"
        );

        createLocationRequest = new CreateLocationRequest(
                "Warehouse A",
                "123 Main Street, City, State 12345",
                "Test Controller",
                "test.email@location.com",
                "+1-555-123-4567"
        );
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getAllLocations_WithValidParameters_ShouldReturnPageOfLocations() throws Exception {
        Page<LocationDto> locationPage = new PageImpl<>(List.of(locationDto));
        when(locationService.getAllLocations(0, 10)).thenReturn(locationPage);

        mockMvc.perform(get("/api/locations")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Warehouse A"))
                .andExpect(jsonPath("$.content[0].contactEmail").value("test.email@location.com"));

        verify(locationService).getAllLocations(0, 10);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getAllLocations_WithDefaultParameters_ShouldReturnPageOfLocations() throws Exception {
        Page<LocationDto> locationPage = new PageImpl<>(List.of(locationDto));
        when(locationService.getAllLocations(0, 10)).thenReturn(locationPage);

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray());

        verify(locationService).getAllLocations(0, 10);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getAllLocations_WithSizeAboveLimit_ShouldClampToMaxSize() throws Exception {
        Page<LocationDto> locationPage = new PageImpl<>(List.of(locationDto));
        when(locationService.getAllLocations(0, 50)).thenReturn(locationPage);

        mockMvc.perform(get("/api/locations")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk());

        verify(locationService).getAllLocations(0, 50);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getAllLocations_WithSizeBelowMinimum_ShouldClampToMinSize() throws Exception {
        Page<LocationDto> locationPage = new PageImpl<>(List.of(locationDto));
        when(locationService.getAllLocations(0, 1)).thenReturn(locationPage);

        mockMvc.perform(get("/api/locations")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isOk());

        verify(locationService).getAllLocations(0, 1);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getAllLocations_WithNegativePage_ShouldClampToZero() throws Exception {
        Page<LocationDto> locationPage = new PageImpl<>(List.of(locationDto));
        when(locationService.getAllLocations(0, 10)).thenReturn(locationPage);

        mockMvc.perform(get("/api/locations")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(locationService).getAllLocations(0, 10);
    }

    @Test
    void getAllLocations_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(locationService);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getLocationById_WithValidId_ShouldReturnLocation() throws Exception {
        when(locationService.getLocationById(1L)).thenReturn(locationDto);

        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Warehouse A"))
                .andExpect(jsonPath("$.address").value("123 Main Street, City, State 12345"))
                .andExpect(jsonPath("$.contactPerson").value("Test Controller"))
                .andExpect(jsonPath("$.contactEmail").value("test.email@location.com"))
                .andExpect(jsonPath("$.contactPhone").value("+1-555-123-4567"));

        verify(locationService).getLocationById(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getLocationById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(locationService.getLocationById(999L))
                .thenThrow(new ResourceNotFoundException("Location not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/locations/999"))
                .andExpect(status().isNotFound());

        verify(locationService).getLocationById(999L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_WRITE")
    void createLocation_WithValidRequest_ShouldReturnCreatedLocation() throws Exception {
        when(locationService.createLocation(any(CreateLocationRequest.class))).thenReturn(locationDto);

        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLocationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Warehouse A"));

        verify(locationService).createLocation(any(CreateLocationRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN_WRITE")
    void createLocation_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateLocationRequest invalidRequest = new CreateLocationRequest(
                "",
                "",
                "",
                "invalid-email",
                ""
        );

        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(locationService);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_WRITE")
    void createLocation_WithBlankName_ShouldReturnBadRequest() throws Exception {
        CreateLocationRequest invalidRequest = new CreateLocationRequest(
                "",
                "123 Main Street",
                "Test Controller",
                "test.email@location.com",
                "+1-555-123-4567"
        );

        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(locationService);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_UPDATE")
    void updateLocation_WithValidRequest_ShouldReturnUpdatedLocation() throws Exception {
        when(locationService.updateLocation(eq(1L), any(CreateLocationRequest.class))).thenReturn(locationDto);

        mockMvc.perform(put("/api/locations/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLocationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Warehouse A"));

        verify(locationService).updateLocation(eq(1L), any(CreateLocationRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN_UPDATE")
    void updateLocation_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(locationService.updateLocation(eq(999L), any(CreateLocationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Location not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/locations/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLocationRequest)))
                .andExpect(status().isNotFound());

        verify(locationService).updateLocation(eq(999L), any(CreateLocationRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN_UPDATE")
    void updateLocation_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateLocationRequest invalidRequest = new CreateLocationRequest(
                "",
                "",
                "",
                "invalid-email",
                ""
        );

        mockMvc.perform(put("/api/locations/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(locationService);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_DELETE")
    void deleteLocationById_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(locationService).deleteLocation(1L);

        mockMvc.perform(delete("/api/locations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(locationService).deleteLocation(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_DELETE")
    void deleteLocationById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Location not found", HttpStatus.NOT_FOUND))
                .when(locationService).deleteLocation(999L);

        mockMvc.perform(delete("/api/locations/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(locationService).deleteLocation(999L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_DELETE")
    void deleteLocationById_WithLocationInUse_ShouldReturnConflict() throws Exception {
        doThrow(new DataIntegrityViolationException("Cannot delete location with existing data"))
                .when(locationService).deleteLocation(1L);

        mockMvc.perform(delete("/api/locations/1")
                        .with(csrf()))
                .andExpect(status().isConflict());

        verify(locationService).deleteLocation(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN_READ")
    void getAllLocations_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        Page<LocationDto> emptyPage = new PageImpl<>(List.of());
        when(locationService.getAllLocations(0, 10)).thenReturn(emptyPage);

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(locationService).getAllLocations(0, 10);
    }
}
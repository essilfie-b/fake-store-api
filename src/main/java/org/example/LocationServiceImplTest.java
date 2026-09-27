package com.supply.chain.inventory.services.impl;

import com.supply.chain.inventory.dtos.CreateLocationRequest;
import com.supply.chain.inventory.dtos.LocationDto;
import com.supply.chain.inventory.entities.Location;
import com.supply.chain.inventory.exceptions.EntityInUseException;
import com.supply.chain.inventory.exceptions.ResourceNotFoundException;
import com.supply.chain.inventory.mappers.LocationMapper;
import com.supply.chain.inventory.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private Location location;

    @InjectMocks
    private LocationServiceImpl locationService;

    private LocationDto locationDto;
    private CreateLocationRequest createLocationRequest;

    @BeforeEach
    void setUp() {
        locationDto = new LocationDto(
                1L,
                "Warehouse A",
                "123 Main Street, City, State 12345",
                "Location Test",
                "test.email@location.com",
                "+1-555-123-4567"
        );

        createLocationRequest = new CreateLocationRequest(
                "Warehouse A",
                "123 Main Street, City, State 12345",
                "Location Test",
                "test.email@location.com",
                "+1-555-123-4567"
        );
    }

    @Test
    void getAllLocations_ShouldReturnPageOfLocationDtos() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> locationPage = new PageImpl<>(List.of(location));

        when(locationRepository.findAll(pageable)).thenReturn(locationPage);
        when(locationMapper.toDto(location)).thenReturn(locationDto);

        Page<LocationDto> result = locationService.getAllLocations(page, size);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(locationDto);
        verify(locationRepository).findAll(pageable);
        verify(locationMapper).toDto(location);
    }

    @Test
    void getAllLocations_WithEmptyResult_ShouldReturnEmptyPage() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> emptyPage = new PageImpl<>(List.of());

        when(locationRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<LocationDto> result = locationService.getAllLocations(page, size);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(locationRepository).findAll(pageable);
        verifyNoInteractions(locationMapper);
    }

    @Test
    void getLocationById_WithValidId_ShouldReturnLocationDto() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(locationMapper.toDto(location)).thenReturn(locationDto);

        LocationDto result = locationService.getLocationById(id);

        assertThat(result).isEqualTo(locationDto);
        verify(locationRepository).findById(id);
        verify(locationMapper).toDto(location);
    }

    @Test
    void getLocationById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        Long id = 999L;

        when(locationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.getLocationById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location not found");

        verify(locationRepository).findById(id);
        verifyNoInteractions(locationMapper);
    }

    @Test
    void getLocationById_WithResourceNotFoundException_ShouldHaveCorrectHttpStatus() {
        Long id = 999L;

        when(locationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.getLocationById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(exception -> {
                    ResourceNotFoundException ex = (ResourceNotFoundException) exception;
                    assertThat(ex.getMessage()).isEqualTo("Location not found");
                });
    }

    @Test
    void createLocation_WithValidRequest_ShouldReturnLocationDto() {
        when(locationMapper.toEntity(createLocationRequest)).thenReturn(location);
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDto(location)).thenReturn(locationDto);

        LocationDto result = locationService.createLocation(createLocationRequest);

        assertThat(result).isEqualTo(locationDto);
        verify(locationMapper).toEntity(createLocationRequest);
        verify(locationRepository).save(location);
        verify(locationMapper).toDto(location);
    }

    @Test
    void updateLocation_WithValidIdAndRequest_ShouldReturnUpdatedLocationDto() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDto(location)).thenReturn(locationDto);

        LocationDto result = locationService.updateLocation(id, createLocationRequest);

        assertThat(result).isEqualTo(locationDto);
        verify(locationRepository).findById(id);
        verify(locationMapper).update(createLocationRequest, location);
        verify(locationRepository).save(location);
        verify(locationMapper).toDto(location);
    }

    @Test
    void updateLocation_WithInvalidId_ShouldThrowResourceNotFoundException() {
        Long id = 999L;

        when(locationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.updateLocation(id, createLocationRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location not found");

        verify(locationRepository).findById(id);
        verifyNoMoreInteractions(locationMapper, locationRepository);
    }

    @Test
    void deleteLocation_WithValidIdAndNoRelatedData_ShouldDeleteLocation() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(location.hasInventories()).thenReturn(false);
        when(location.hasAlertThresholds()).thenReturn(false);
        when(location.hasDamageReports()).thenReturn(false);

        locationService.deleteLocation(id);

        verify(locationRepository).findById(id);
        verify(location).hasInventories();
        verify(location).hasAlertThresholds();
        verify(location).hasDamageReports();
        verify(locationRepository).delete(location);
    }

    @Test
    void deleteLocation_WithInvalidId_ShouldThrowResourceNotFoundException() {
        Long id = 999L;

        when(locationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.deleteLocation(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location not found");

        verify(locationRepository).findById(id);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void deleteLocation_WithInventories_ShouldThrowEntityInUseException() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(location.hasInventories()).thenReturn(true);

        assertThatThrownBy(() -> locationService.deleteLocation(id))
                .isInstanceOf(EntityInUseException.class)
                .hasMessage("Cannot delete location with existing data");

        verify(locationRepository).findById(id);
        verify(location).hasInventories();
        verify(locationRepository, never()).delete(any());
    }

    @Test
    void deleteLocation_WithAlertThresholds_ShouldThrowEntityInUseException() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(location.hasInventories()).thenReturn(false);
        when(location.hasAlertThresholds()).thenReturn(true);

        assertThatThrownBy(() -> locationService.deleteLocation(id))
                .isInstanceOf(EntityInUseException.class)
                .hasMessage("Cannot delete location with existing data");

        verify(locationRepository).findById(id);
        verify(location).hasInventories();
        verify(location).hasAlertThresholds();
        verify(locationRepository, never()).delete(any());
    }

    @Test
    void deleteLocation_WithDamageReports_ShouldThrowEntityInUseException() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(location.hasInventories()).thenReturn(false);
        when(location.hasAlertThresholds()).thenReturn(false);
        when(location.hasDamageReports()).thenReturn(true);

        assertThatThrownBy(() -> locationService.deleteLocation(id))
                .isInstanceOf(EntityInUseException.class)
                .hasMessage("Cannot delete location with existing data");

        verify(locationRepository).findById(id);
        verify(location).hasInventories();
        verify(location).hasAlertThresholds();
        verify(location).hasDamageReports();
        verify(locationRepository, never()).delete(any());
    }

    @Test
    void deleteLocation_WithMultipleRelatedData_ShouldThrowEntityInUseExceptionOnFirstCheck() {
        Long id = 1L;

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        when(location.hasInventories()).thenReturn(true);

        assertThatThrownBy(() -> locationService.deleteLocation(id))
                .isInstanceOf(EntityInUseException.class)
                .hasMessage("Cannot delete location with existing data");

        verify(locationRepository).findById(id);
        verify(location).hasInventories();
        verify(location, never()).hasAlertThresholds();
        verify(location, never()).hasDamageReports();
        verify(locationRepository, never()).delete(any());
    }

    @Test
    void createLocation_WithDifferentValidData_ShouldReturnLocationDto() {
        CreateLocationRequest differentRequest = new CreateLocationRequest(
                "Distribution Center B",
                "456 Oak Avenue, Another City, State 67890",
                "Jane Smith",
                "jane.smith@company.com",
                "+1-555-987-6543"
        );

        LocationDto differentLocationDto = new LocationDto(
                2L,
                "Distribution Center B",
                "456 Oak Avenue, Another City, State 67890",
                "Jane Smith",
                "jane.smith@company.com",
                "+1-555-987-6543"
        );

        Location differentLocation = mock(Location.class);

        when(locationMapper.toEntity(differentRequest)).thenReturn(differentLocation);
        when(locationRepository.save(differentLocation)).thenReturn(differentLocation);
        when(locationMapper.toDto(differentLocation)).thenReturn(differentLocationDto);

        LocationDto result = locationService.createLocation(differentRequest);

        assertThat(result).isEqualTo(differentLocationDto);
        assertThat(result.name()).isEqualTo("Distribution Center B");
        assertThat(result.contactEmail()).isEqualTo("jane.smith@company.com");
    }

    @Test
    void getAllLocations_WithMultipleLocations_ShouldReturnAllLocations() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Location location2 = mock(Location.class);
        LocationDto locationDto2 = new LocationDto(
                2L,
                "Warehouse B",
                "789 Pine Street, City, State 54321",
                "Alice Johnson",
                "alice.johnson@company.com",
                "+1-555-456-7890"
        );

        Page<Location> locationPage = new PageImpl<>(List.of(location, location2));

        when(locationRepository.findAll(pageable)).thenReturn(locationPage);
        when(locationMapper.toDto(location)).thenReturn(locationDto);
        when(locationMapper.toDto(location2)).thenReturn(locationDto2);

        Page<LocationDto> result = locationService.getAllLocations(page, size);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(locationDto, locationDto2);
        verify(locationRepository).findAll(pageable);
        verify(locationMapper).toDto(location);
        verify(locationMapper).toDto(location2);
    }
}
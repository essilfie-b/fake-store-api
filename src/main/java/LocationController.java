package com.supply.chain.inventory.controllers;

import com.supply.chain.inventory.dtos.CreateLocationRequest;
import com.supply.chain.inventory.dtos.LocationDto;
import com.supply.chain.inventory.services.LocationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@AllArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<Page<LocationDto>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        size = Math.clamp(size, 1, 50);
        page = Math.clamp(page, 0, Integer.MAX_VALUE);

        Page<LocationDto> locations = locationService.getAllLocations(page, size);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        return ResponseEntity.ok(locationService.createLocation(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody CreateLocationRequest request
    ) {
        return ResponseEntity.ok(locationService.updateLocation(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

}

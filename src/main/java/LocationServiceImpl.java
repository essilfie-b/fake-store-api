package com.supply.chain.inventory.services.impl;

import com.supply.chain.inventory.dtos.CreateLocationRequest;
import com.supply.chain.inventory.dtos.LocationDto;
import com.supply.chain.inventory.entities.Location;
import com.supply.chain.inventory.exceptions.EntityInUseException;
import com.supply.chain.inventory.exceptions.ResourceNotFoundException;
import com.supply.chain.inventory.mappers.LocationMapper;
import com.supply.chain.inventory.repositories.LocationRepository;
import com.supply.chain.inventory.services.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    private static final String LOCATION_NOT_FOUND = "Location not found";
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<LocationDto> getAllLocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> locationPage = locationRepository.findAll(pageable);

        return locationPage.map(locationMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public LocationDto getLocationById(Long id) {
        var location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND, HttpStatus.NOT_FOUND));

        return locationMapper.toDto(location);
    }

    @Override
    public LocationDto createLocation(CreateLocationRequest request) {
        var location = locationMapper.toEntity(request);

        locationRepository.save(location);

        return locationMapper.toDto(location);
    }

    @Override
    public LocationDto updateLocation(Long id, CreateLocationRequest request) {
        var location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND, HttpStatus.NOT_FOUND));

        locationMapper.update(request, location);

        locationRepository.save(location);

        return locationMapper.toDto(location);

    }

    @Override
    public void deleteLocation(Long id) {
        var location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LOCATION_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (location.hasInventories() ||
                location.hasAlertThresholds() ||
                location.hasDamageReports()) {
            throw new EntityInUseException("Cannot delete location with existing data");
        }

        locationRepository.delete(location);
    }
}

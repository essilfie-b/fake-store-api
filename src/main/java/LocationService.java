package com.supply.chain.inventory.services;

import com.supply.chain.inventory.dtos.CreateLocationRequest;
import com.supply.chain.inventory.dtos.LocationDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface LocationService {
    @Transactional(readOnly = true)
    Page<LocationDto> getAllLocations(int page, int size);

    @Transactional(readOnly = true)
    LocationDto getLocationById(Long id);

    LocationDto createLocation(CreateLocationRequest request);

    LocationDto updateLocation(Long id, CreateLocationRequest request);

    void deleteLocation(Long id);
}

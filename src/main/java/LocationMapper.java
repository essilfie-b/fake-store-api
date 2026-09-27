package com.supply.chain.inventory.mappers;

import com.supply.chain.inventory.dtos.CreateLocationRequest;
import com.supply.chain.inventory.dtos.LocationDto;
import com.supply.chain.inventory.entities.Location;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toEntity(CreateLocationRequest request);

    LocationDto toDto(Location location);

    Location update(CreateLocationRequest createLocationRequest, @MappingTarget Location location);
}
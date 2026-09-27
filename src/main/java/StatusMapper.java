package com.supply.chain.inventory.mappers;

import com.supply.chain.inventory.dtos.CreateStatusRequest;
import com.supply.chain.inventory.dtos.StatusDto;
import com.supply.chain.inventory.entities.Status;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    Status toStatus(CreateStatusRequest request);

    StatusDto toDto(Status status);

    void updateStatus(CreateStatusRequest request, @MappingTarget Status status);
}

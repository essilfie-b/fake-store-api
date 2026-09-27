package com.supply.chain.inventory.services;

import com.supply.chain.inventory.dtos.CreateStatusRequest;
import com.supply.chain.inventory.dtos.StatusDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface StatusService {
    @Transactional(readOnly = true)
    Page<StatusDto> getAllStatuses(int page, int size);

    @Transactional(readOnly = true)
    StatusDto getStatusById(Integer id);

    StatusDto addStatus(CreateStatusRequest request);

    StatusDto updateStatus(Integer id, CreateStatusRequest request);

    void deleteStatus(Integer id);
}

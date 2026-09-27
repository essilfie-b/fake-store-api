package com.supply.chain.inventory.services.impl;

import com.supply.chain.inventory.dtos.CreateStatusRequest;
import com.supply.chain.inventory.dtos.StatusDto;
import com.supply.chain.inventory.entities.Status;
import com.supply.chain.inventory.exceptions.ResourceNotFoundException;
import com.supply.chain.inventory.mappers.StatusMapper;
import com.supply.chain.inventory.repositories.StatusRepository;
import com.supply.chain.inventory.services.StatusService;
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
public class StatusServiceImpl implements StatusService {
    private static final String STATUS_NOT_FOUND = "Status not found";
    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<StatusDto> getAllStatuses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Status> statusPage = statusRepository.findAll(pageable);

        return statusPage.map(statusMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public StatusDto getStatusById(Integer id) {
        return statusRepository.findById(id)
                .map(statusMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND, HttpStatus.NOT_FOUND));

    }

    @Override
    public StatusDto addStatus(CreateStatusRequest request) {
        var status = statusMapper.toStatus(request);
        statusRepository.save(status);

        return statusMapper.toDto(status);
    }

    @Override
    public StatusDto updateStatus(Integer id, CreateStatusRequest request) {
        var status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND, HttpStatus.NOT_FOUND));
        statusMapper.updateStatus(request, status);

        statusRepository.save(status);

        return statusMapper.toDto(status);
    }

    @Override
    public void deleteStatus(Integer id) {
        var status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND, HttpStatus.NOT_FOUND));

        statusRepository.delete(status);
    }
}

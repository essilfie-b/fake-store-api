package com.supply.chain.inventory.controllers;

import com.supply.chain.inventory.dtos.CreateStatusRequest;
import com.supply.chain.inventory.dtos.StatusDto;
import com.supply.chain.inventory.services.StatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/status")
@AllArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<Page<StatusDto>> getAllStatuses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        size = Math.clamp(size, 1, 50);
        page = Math.clamp(page, 0, Integer.MAX_VALUE);

        Page<StatusDto> statuses = statusService.getAllStatuses(page, size);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<StatusDto> getStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(statusService.getStatusById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ResponseEntity<StatusDto> addStatus(@Valid @RequestBody CreateStatusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(statusService.addStatus(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<StatusDto> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody CreateStatusRequest request) {
        return ResponseEntity.ok(statusService.updateStatus(id, request));

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<Void> deleteStatus(@PathVariable Integer id) {
        statusService.deleteStatus(id);

        return ResponseEntity.noContent().build();
    }



}

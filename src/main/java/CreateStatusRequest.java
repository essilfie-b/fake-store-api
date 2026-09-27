package com.supply.chain.inventory.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateStatusRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Color code is required")
        String colorCode) {
}
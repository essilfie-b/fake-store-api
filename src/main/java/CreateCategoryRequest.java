package com.supply.chain.inventory.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description
) {
}
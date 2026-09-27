package com.supply.chain.inventory.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateLocationRequest(@NotBlank(message = "Name cannot be blank") String name,
                                    @NotBlank(message = "Address cannot be blank") String address,
                                    @NotBlank(message = "Contact person cannot be blank") String contactPerson,
                                    @Email(message = "Email must be valid") @NotBlank(message = "Email cannot be blank") String contactEmail,
                                    @NotBlank(message = "Contact phone cannot be blank") String contactPhone) {
}
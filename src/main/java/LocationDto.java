package com.supply.chain.inventory.dtos;

public record LocationDto(Long id, String name, String address, String contactPerson,
                          String contactEmail, String contactPhone)
{}
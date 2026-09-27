package com.supply.chain.inventory.dtos;


import java.time.LocalDateTime;


public record StatusDto (
        Long id,
        String name,
        String description,
        String colorCode,
        Boolean isActive,
        LocalDateTime createdAt
){
    
}
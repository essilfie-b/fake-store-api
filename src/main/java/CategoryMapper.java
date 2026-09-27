package com.supply.chain.inventory.mappers;

import com.supply.chain.inventory.dtos.CategoryDto;
import com.supply.chain.inventory.dtos.CreateCategoryRequest;
import com.supply.chain.inventory.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CreateCategoryRequest request);

    CategoryDto toDto(Category category);

    Category update(CreateCategoryRequest createCategoryRequest, @MappingTarget Category category);
}
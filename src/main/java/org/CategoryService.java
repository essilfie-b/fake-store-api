package com.supply.chain.inventory.services;

import com.supply.chain.inventory.dtos.CategoryDto;
import com.supply.chain.inventory.dtos.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {
    @Transactional(readOnly = true)
    Page<CategoryDto> getAllCategories(int page, int size);

    @Transactional(readOnly = true)
    CategoryDto getCategoryById(Integer id);

    CategoryDto createCategory(CreateCategoryRequest request);

    CategoryDto updateCategory(Integer id, CreateCategoryRequest request);

    void deleteCategory(Integer id);
}

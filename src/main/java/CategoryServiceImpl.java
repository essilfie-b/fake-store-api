package com.supply.chain.inventory.services.impl;

import com.supply.chain.inventory.dtos.CategoryDto;
import com.supply.chain.inventory.dtos.CreateCategoryRequest;
import com.supply.chain.inventory.entities.Category;
import com.supply.chain.inventory.exceptions.EntityInUseException;
import com.supply.chain.inventory.exceptions.ResourceNotFoundException;
import com.supply.chain.inventory.mappers.CategoryMapper;
import com.supply.chain.inventory.repositories.CategoryRepository;
import com.supply.chain.inventory.services.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_NOT_FOUND = "Category not found";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<CategoryDto> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Integer id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto createCategory(CreateCategoryRequest request) {
        var category = categoryMapper.toEntity(request);

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(Integer id, CreateCategoryRequest request) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        categoryMapper.update(request, category);

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (category.hasProducts()) {
            throw new EntityInUseException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
    }
}
package com.supply.chain.inventory.controllers;

import com.supply.chain.inventory.dtos.CategoryDto;
import com.supply.chain.inventory.dtos.CreateCategoryRequest;
import com.supply.chain.inventory.services.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        size = Math.clamp(size, 1, 50);
        page = Math.clamp(page, 0, Integer.MAX_VALUE);

        Page<CategoryDto> categories = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
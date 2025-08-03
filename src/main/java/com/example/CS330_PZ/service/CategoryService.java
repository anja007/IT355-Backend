package com.example.CS330_PZ.service;

import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
    public Category addCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryName(categoryName);

        return categoryRepository.save(category);
    }
}

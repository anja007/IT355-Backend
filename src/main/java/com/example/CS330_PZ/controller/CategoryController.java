package com.example.CS330_PZ.controller;

import com.example.CS330_PZ.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    //da l uopste koristim
    @GetMapping("{categoryId}")
    public ResponseEntity<?> getCategoryByCategoryId(){
        return ResponseEntity.ok(categoryService.getCategoryByCategoryId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add")
    public ResponseEntity<?> addCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(categoryService.addCategory(categoryName));
    }

}

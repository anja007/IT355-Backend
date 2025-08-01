package com.example.CS330_PZ.repository;

import com.example.CS330_PZ.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c")
    List<Category> getAllCategories();

}

package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.entity.Category;
import com.aurelius.tech.eventmanagementservice.exception.BusinessException;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new BusinessException("Category with name already exists");
        }
        return categoryRepository.save(category);
    }
    
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional
    public Category updateCategory(UUID id, Category categoryDetails) {
        Category category = getCategoryById(id);
        if (!category.getName().equals(categoryDetails.getName()) && 
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new BusinessException("Category with name already exists");
        }
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}






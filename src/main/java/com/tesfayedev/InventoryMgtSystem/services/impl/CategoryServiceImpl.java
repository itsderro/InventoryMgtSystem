package com.tesfayedev.InventoryMgtSystem.services.impl;


import com.tesfayedev.InventoryMgtSystem.dtos.CategoryDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.exceptions.NotFoundException;
import com.tesfayedev.InventoryMgtSystem.models.Category;
import com.tesfayedev.InventoryMgtSystem.repositories.CategoryRepository;
import com.tesfayedev.InventoryMgtSystem.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Override
    public Response createCategory(CategoryDTO categoryDTO) {

        Category categoryToSave = modelMapper.map(categoryDTO, Category.class);
        categoryRepository.save(categoryToSave);
        return Response.builder()
                .status(200)
                .message("Category saved successfully")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));

        categories.forEach(category -> category.setProducts(null));
        List<CategoryDTO> categoryDTOList = modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());

        return  Response.builder()
                .status(200)
                .message("success")
                .categories(categoryDTOList)
                .build();
    }

    @Override
    public Response getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Category Not Found"));
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .category(categoryDTO)
                .build();
    }

    @Override
    public Response updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Category not found"));

        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return Response.builder()
                .status(200)
                .message("Category was successfully updated")
                .build();
    }

    @Override
    public Response deleteCategory(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Category not found"));
        categoryRepository.deleteById(id);
        return Response.builder()
                .status(200)
                .message("Category was successfully deleted")
                .build();
    }
}

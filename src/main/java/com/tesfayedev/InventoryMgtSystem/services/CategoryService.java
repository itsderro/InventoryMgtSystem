package com.tesfayedev.InventoryMgtSystem.services;

import com.tesfayedev.InventoryMgtSystem.dtos.CategoryDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryDTO);

    Response getAllCategories();

    Response getCategoryById(Long id);

    Response updateCategory(Long id, CategoryDTO categoryDTO);

    Response deleteCategory(Long id);
}

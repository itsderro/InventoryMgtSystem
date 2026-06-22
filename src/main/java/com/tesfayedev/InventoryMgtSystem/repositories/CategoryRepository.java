package com.tesfayedev.InventoryMgtSystem.repositories;

import com.tesfayedev.InventoryMgtSystem.models.Category;
import com.tesfayedev.InventoryMgtSystem.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}

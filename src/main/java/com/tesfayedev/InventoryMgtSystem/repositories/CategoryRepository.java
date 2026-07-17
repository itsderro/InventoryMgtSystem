package com.tesfayedev.InventoryMgtSystem.repositories;

import com.tesfayedev.InventoryMgtSystem.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category,Long> {
}

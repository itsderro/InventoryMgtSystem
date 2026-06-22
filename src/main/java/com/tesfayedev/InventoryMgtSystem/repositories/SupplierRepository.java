package com.tesfayedev.InventoryMgtSystem.repositories;

import com.tesfayedev.InventoryMgtSystem.models.Product;
import com.tesfayedev.InventoryMgtSystem.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
}

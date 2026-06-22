package com.tesfayedev.InventoryMgtSystem.repositories;

import com.tesfayedev.InventoryMgtSystem.models.Product;
import com.tesfayedev.InventoryMgtSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}

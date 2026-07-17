package com.tesfayedev.InventoryMgtSystem.repositories;

import com.tesfayedev.InventoryMgtSystem.enums.DeliveryStatus;
import com.tesfayedev.InventoryMgtSystem.models.Product;
import com.tesfayedev.InventoryMgtSystem.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long>, JpaSpecificationExecutor<Transaction> {

    boolean existsByDeliveryPersonnel_Id(Long deliveryPersonnelId);

    List<Transaction> findByDeliveryPersonnel_IdAndDeliveryStatusAndCreatedAtBetween(
            Long deliveryPersonnelId,
            DeliveryStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}

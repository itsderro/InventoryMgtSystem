package com.tesfayedev.InventoryMgtSystem.dtos;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionItemRequestDTO {
    @Positive(message = "Product id is required")
    private Long productId;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}

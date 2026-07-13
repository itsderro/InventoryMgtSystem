package com.tesfayedev.InventoryMgtSystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tesfayedev.InventoryMgtSystem.enums.PriceType;
import com.tesfayedev.InventoryMgtSystem.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {
    @Positive(message="Product id is required")
    private Long productId;

    @Positive(message="quantity is required")
    private Integer quantity;

    @Positive(message="supplier id is required")
    private Long supplierId;

    private String description;

    private String note;

    private PriceType priceType;

}

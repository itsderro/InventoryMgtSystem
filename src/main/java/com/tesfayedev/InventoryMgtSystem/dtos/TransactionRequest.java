package com.tesfayedev.InventoryMgtSystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tesfayedev.InventoryMgtSystem.enums.PriceType;
import com.tesfayedev.InventoryMgtSystem.enums.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

    @Positive(message="supplier id is required")
    private Long supplierId;

    private String description;

    private String note;

    private PriceType priceType;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<TransactionItemRequestDTO> items;

}

package com.tesfayedev.InventoryMgtSystem.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionStatus;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    private Long id;

    private Integer totalProducts;

    private BigDecimal totalPrice;


    private TransactionType transactionType;


    private TransactionStatus transactionStatus;

    private String description;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    private ProductDTO product;

    private UserSummaryDTO user;

    private SupplierDTO supplier;

}

package com.tesfayedev.InventoryMgtSystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonnelDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private BigDecimal baseSalary;
    private Boolean active;
}

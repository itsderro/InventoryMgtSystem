package com.tesfayedev.InventoryMgtSystem.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SalaryReportDTO {
    private Long deliveryPersonnelId;
    private String fullName;
    private int numberOfDeliveries;
    private BigDecimal totalDeliveredAmount;
    private BigDecimal totalCommission;
    private BigDecimal totalSalary;
}

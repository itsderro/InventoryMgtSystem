package com.tesfayedev.InventoryMgtSystem.services.impl;

import com.tesfayedev.InventoryMgtSystem.dtos.DeliveryPersonnelDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.SalaryReportDTO;
import com.tesfayedev.InventoryMgtSystem.enums.DeliveryStatus;
import com.tesfayedev.InventoryMgtSystem.models.DeliveryPersonnel;
import com.tesfayedev.InventoryMgtSystem.models.Transaction;
import com.tesfayedev.InventoryMgtSystem.repositories.DeliveryPersonnelRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryPersonnelService {
    private final DeliveryPersonnelRepository personnelRepository;
    private final TransactionRepository transactionRepository;
    private final SalaryService salaryService;
    private static final BigDecimal DEFAULT_BASE_SALARY = new BigDecimal("0.00");

    public DeliveryPersonnelDTO addDeliveryPersonnel(DeliveryPersonnelDTO dto){
        DeliveryPersonnel deliveryPersonnel = DeliveryPersonnel.builder()
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .active(true)
                .baseSalary(DEFAULT_BASE_SALARY)
                .build();

        DeliveryPersonnel saved = personnelRepository.save(deliveryPersonnel);

        return DeliveryPersonnelDTO.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .phoneNumber(saved.getPhoneNumber())
                .active(saved.isActive())
                .baseSalary(saved.getBaseSalary())
                .build();
    }

    public void deletePersonnel(Long id){
        //Enforce referential integrity
        if (transactionRepository.existsByDeliveryPersonnel_Id(id)){
            throw new IllegalStateException("Cannot delete personnel with existing transaction history. Deactivate them instead.");
        }
        personnelRepository.deleteById(id);
    }

    public SalaryReportDTO generateSalaryReport(Long personnelId, LocalDateTime start,LocalDateTime end){
        DeliveryPersonnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(()-> new EntityNotFoundException("Personnel not found"));

        List<Transaction> deliveries = transactionRepository
                .findByDeliveryPersonnel_IdAndDeliveryStatusAndCreatedAtBetween(
                        personnelId, DeliveryStatus.DELIVERED,start,end
                );

        BigDecimal totalDeliveredAmount = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;

        for(Transaction txn : deliveries){
            totalDeliveredAmount = totalDeliveredAmount.add(txn.getTotalPrice());
            totalCommission = totalCommission.add(salaryService.calculateCommission(txn));
        }

        BigDecimal totalSalary = personnel.getBaseSalary().add(totalCommission);

        return SalaryReportDTO.builder()
                .deliveryPersonnelId(personnel.getId())
                .fullName(personnel.getFullName())
                .numberOfDeliveries(deliveries.size())
                .totalDeliveredAmount(totalDeliveredAmount)
                .totalCommission(totalCommission)
                .totalSalary(totalSalary)
                .build();
    }
}

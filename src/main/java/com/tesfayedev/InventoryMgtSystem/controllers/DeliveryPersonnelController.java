package com.tesfayedev.InventoryMgtSystem.controllers;

import com.tesfayedev.InventoryMgtSystem.dtos.DeliveryPersonnelDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.dtos.SalaryReportDTO;
import com.tesfayedev.InventoryMgtSystem.services.impl.DeliveryPersonnelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/delivery_personnel")
@RequiredArgsConstructor
public class DeliveryPersonnelController {

    private final DeliveryPersonnelService deliveryPersonnelService;

    @GetMapping("/{id}/salary")
    public ResponseEntity<Response> getSalaryReport(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end
            ){
        SalaryReportDTO reportDTO = deliveryPersonnelService.generateSalaryReport(id,start,end);
        return ResponseEntity.ok(
                Response.builder()
                        .status(200)
                        .message("Salary report generated successfully")
                        .salaryReportDTO(reportDTO)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deletePersonnel(@PathVariable Long id){
        deliveryPersonnelService.deletePersonnel(id);
        return ResponseEntity.ok(
                Response.builder()
                        .status(200)
                        .message("Personnel deleted successfully")
                        .build()
        );
    }

    @PostMapping("/addPersonnel")
    public ResponseEntity<Response> addDeliveryPersonnel(@RequestBody @Valid DeliveryPersonnelDTO dto){
        DeliveryPersonnelDTO created = deliveryPersonnelService.addDeliveryPersonnel(dto);
        return ResponseEntity.ok(
                Response.builder()
                        .status(200)
                        .message("Delivery personnel added successfully")
                        .deliveryPersonnelDTO(created)
                        .build()
        );
    }
}

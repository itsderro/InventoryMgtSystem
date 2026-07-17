package com.tesfayedev.InventoryMgtSystem.controllers;


import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionRequest;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionStatus;
import com.tesfayedev.InventoryMgtSystem.models.Transaction;
import com.tesfayedev.InventoryMgtSystem.repositories.TransactionRepository;
import com.tesfayedev.InventoryMgtSystem.services.TransactionService;
import com.tesfayedev.InventoryMgtSystem.services.impl.ReceiptService;
import com.tesfayedev.InventoryMgtSystem.services.impl.TransactionServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServiceImpl transactionServiceImpl;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final ReceiptService receiptService;

    @PostMapping("/purchase")
    public ResponseEntity<Response> purchaseInventory(@RequestBody @Valid TransactionRequest transactionRequest){
        return  ResponseEntity.ok(transactionService.purchase(transactionRequest));
    }

    @PostMapping("/sell")
    public ResponseEntity<Response> makeSale(@RequestBody @Valid TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.sell(transactionRequest));
    }

    @PostMapping("/return")
    public ResponseEntity<Response> returnToSupplier(@RequestBody @Valid TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.returnToSupplier(transactionRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(required = false) String searchValue
    ){
        return ResponseEntity.ok(transactionService.getAllTransactions(page,size,searchValue));
    }

    @GetMapping("{id}")
    public ResponseEntity<Response> getTransactionById(@PathVariable Long id){
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/by-month-year")
    public ResponseEntity<Response> getTransactionByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year){
        return ResponseEntity.ok(transactionService.getTransactionByMonthAndYear(month, year));
    }

    @PutMapping("/{transactionId}")
    public  ResponseEntity<Response> updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestBody TransactionStatus status){
        return ResponseEntity.ok(transactionService.updateTransactionStatus(transactionId,status));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long id){
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Transaction not found"));

        byte[] pdf = receiptService.generateReceiptPdf(txn);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=receipt-"+id+".pdf")
                .body(pdf);
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<Response> markAsDelivered(
            @PathVariable Long id,
            @RequestParam Long deliveryPersonnelId
    ){
        TransactionDTO updatedTxn = transactionServiceImpl.markAsDelivered(id, deliveryPersonnelId);

        return ResponseEntity.ok(
                Response.builder()
                        .status(200)
                        .message("Transaction marked as delivered")
                        .transaction(updatedTxn)
                        .build()
        );

    }
}

package com.tesfayedev.InventoryMgtSystem.services;

import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionRequest;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionStatus;

public interface TransactionService {

    Response purchase(TransactionRequest transactionRequest);

    Response sell(TransactionRequest transactionRequest);

    Response returnToSupplier(TransactionRequest transactionRequest);

    Response getAllTransactions(int page, int size, String filter);

    Response getTransactionById(Long id);

    Response getTransactionByMonthAndYear(int month, int year);

    Response updateTransactionStatus(Long transactionId, TransactionStatus status);
}

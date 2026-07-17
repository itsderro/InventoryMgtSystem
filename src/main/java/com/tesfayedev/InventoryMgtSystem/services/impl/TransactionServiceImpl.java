package com.tesfayedev.InventoryMgtSystem.services.impl;

import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionItemRequestDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionRequest;
import com.tesfayedev.InventoryMgtSystem.enums.DeliveryStatus;
import com.tesfayedev.InventoryMgtSystem.enums.PriceType;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionStatus;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionType;
import com.tesfayedev.InventoryMgtSystem.exceptions.NameValueRequiredException;
import com.tesfayedev.InventoryMgtSystem.exceptions.NotFoundException;
import com.tesfayedev.InventoryMgtSystem.models.*;
import com.tesfayedev.InventoryMgtSystem.repositories.DeliveryPersonnelRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.ProductRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.SupplierRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.TransactionRepository;
import com.tesfayedev.InventoryMgtSystem.services.TransactionService;
import com.tesfayedev.InventoryMgtSystem.services.UserService;
import com.tesfayedev.InventoryMgtSystem.specifications.TransactionFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final DeliveryPersonnelRepository deliveryPersonnelRepository;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response purchase(TransactionRequest transactionRequest) {

        Long supplierId = transactionRequest.getSupplierId();
        PriceType priceType = transactionRequest.getPriceType();

        if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");
        if (transactionRequest.getItems() == null || transactionRequest.getItems().isEmpty())
            throw new NameValueRequiredException("At least one item is required");

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new NotFoundException("Supplier not found"));

        User user = userService.getCurrentLoggedInUser();

        //create a transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PURCHASE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .priceType(priceType)
                .user(user)
                .supplier(supplier)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        List<TransactionItem> items = new ArrayList<>();
        int totalProducts = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (TransactionItemRequestDTO transactionItemRequestDTO : transactionRequest.getItems()){
            Product product = productRepository.findById(transactionItemRequestDTO.getProductId())
                    .orElseThrow(()-> new NotFoundException("Product not found: "+ transactionItemRequestDTO.getProductId()));

            Integer quantity = transactionItemRequestDTO.getQuantity();

            product.setStockQuantity(product.getStockQuantity()+quantity);
            productRepository.save(product);

            BigDecimal unitPrice = product.getCostPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            items.add(TransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .priceType(priceType)
                    .subtotal(subtotal)
                    .build()
            );

            totalProducts += quantity;
            totalPrice = totalPrice.add(subtotal);
        }

        transaction.setItems(items);
        transaction.setTotalProducts(totalProducts);
        transaction.setTotalPrice(totalPrice);

        transactionRepository.save(transaction);
        return Response.builder()
                .status(200)
                .message("Purchase was successful")
                .build();

    }

    @Override
    @Transactional
    public Response sell(TransactionRequest transactionRequest) {

        PriceType priceType = transactionRequest.getPriceType();

        if(priceType == null) throw new NameValueRequiredException("Price type is required (RETAIL or WHOLESALE)");
        if(transactionRequest.getItems()==null||transactionRequest.getItems().isEmpty())
            throw new NameValueRequiredException("At least one item is required");

        User user = userService.getCurrentLoggedInUser();

        //create a transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.SALE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .priceType(priceType)
                .user(user)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        List<TransactionItem> items = new ArrayList<>();
        int totalProducts = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (TransactionItemRequestDTO itemRequestDTO : transactionRequest.getItems()){
            Product product = productRepository.findById(itemRequestDTO.getProductId())
                    .orElseThrow(()-> new NotFoundException("Product not found: "+ itemRequestDTO.getProductId()));

            Integer quantity = itemRequestDTO.getQuantity();

            BigDecimal unitPrice = switch (priceType) {
                case RETAIL -> product.getRetailPrice();
                case WHOLESALE -> product.getWholeSalePrice();
                case COST_PRICE -> product.getCostPrice();
            };

            product.setStockQuantity(product.getStockQuantity()-quantity);
            productRepository.save(product);

            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            items.add(TransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .priceType(priceType)
                    .subtotal(subtotal)
                    .build()
            );

            totalProducts += quantity;
            totalPrice = totalPrice.add(subtotal);
            transaction.setItems(items);
            transaction.setTotalProducts(totalProducts);
            transaction.setTotalPrice(totalPrice);

            transactionRepository.save(transaction);
        }


        transactionRepository.save(transaction);
        return Response.builder()
                .status(200)
                .message("Sale was successful")
                .build();

    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {

        Long supplierId = transactionRequest.getSupplierId();

        if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");
        if (transactionRequest.getItems() == null || transactionRequest.getItems().isEmpty())
            throw new NameValueRequiredException("At least one item is required");

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new NotFoundException("Supplier not found"));

        User user = userService.getCurrentLoggedInUser();

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                .transactionStatus(TransactionStatus.PROCESSING)
                .user(user)
                .supplier(supplier)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        List<TransactionItem> items = new ArrayList<>();
        int totalProducts = 0;

        for (TransactionItemRequestDTO itemRequestDTO : transactionRequest.getItems()){
            Product product = productRepository.findById(itemRequestDTO.getProductId())
                    .orElseThrow(()-> new NotFoundException("Product not found: "+itemRequestDTO.getProductId()));

            Integer quantity = itemRequestDTO.getQuantity();

            product.setStockQuantity(product.getStockQuantity()-quantity);
            productRepository.save(product);

            items.add(TransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(BigDecimal.ZERO)
                    .subtotal(BigDecimal.ZERO)
                    .build()
            );

            totalProducts += quantity;
        }

        transaction.setItems(items);
        transaction.setTotalProducts(totalProducts);
        transaction.setTotalPrice(BigDecimal.ZERO);

        transactionRepository.save(transaction);

        return Response.builder()
                .status(200)
                .message("Product return in progress")
                .build();
    }

    @Override
    public Response getAllTransactions(int page, int size, String filter) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id"));

        Specification<Transaction> spec = TransactionFilter.byFilter(filter);
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionDTO> transactionDTOS = modelMapper.map(transactionPage.getContent(), new TypeToken<List<TransactionDTO>>() {}.getType());

        transactionDTOS.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProduct(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactions(transactionDTOS)
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .build();
    }

    @Override
    public Response getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Transaction Not Found"));

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);

        transactionDTO.setUser(null);

        return Response.builder()
                .status(200)
                .message("success")
                .transaction(transactionDTO)
                .build();
    }

    @Override
    public Response getTransactionByMonthAndYear(int month, int year) {
        List<Transaction> transactions = transactionRepository.findAll(TransactionFilter.byMonthAndYear(month,year));

        List<TransactionDTO> transactionDTOS = modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {}.getType());

        transactionDTOS.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProduct(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                .status(200)
                .message("success")
                .transactions(transactionDTOS)
                .build();
    }

    @Override
    public Response updateTransactionStatus(Long transactionId, TransactionStatus status) {

        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new NotFoundException("Transaction not found"));

        existingTransaction.setTransactionStatus(status);
        existingTransaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(existingTransaction);

        return Response.builder()
                .status(200)
                .message("Transaction has been updated")
                .build();

    }

    public TransactionDTO markAsDelivered(Long transactionId, Long personnelId){
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new NotFoundException("Transaction not found"));

        DeliveryPersonnel personnel = deliveryPersonnelRepository.findById(personnelId)
                .orElseThrow(()-> new NotFoundException("Delivery personnel not found"));

        txn.setDeliveryStatus(DeliveryStatus.DELIVERED);
        txn.setDeliveryPersonnel(personnel);

        Transaction savedTxn = transactionRepository.save(txn);
        return modelMapper.map(savedTxn, TransactionDTO.class);
    }
}

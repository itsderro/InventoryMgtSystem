package com.tesfayedev.InventoryMgtSystem.services.impl;

import com.tesfayedev.InventoryMgtSystem.dtos.Response;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionDTO;
import com.tesfayedev.InventoryMgtSystem.dtos.TransactionRequest;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionStatus;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionType;
import com.tesfayedev.InventoryMgtSystem.exceptions.NameValueRequiredException;
import com.tesfayedev.InventoryMgtSystem.exceptions.NotFoundException;
import com.tesfayedev.InventoryMgtSystem.models.Product;
import com.tesfayedev.InventoryMgtSystem.models.Supplier;
import com.tesfayedev.InventoryMgtSystem.models.Transaction;
import com.tesfayedev.InventoryMgtSystem.models.User;
import com.tesfayedev.InventoryMgtSystem.repositories.ProductRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.SupplierRepository;
import com.tesfayedev.InventoryMgtSystem.repositories.TransactionRepository;
import com.tesfayedev.InventoryMgtSystem.services.TransactionService;
import com.tesfayedev.InventoryMgtSystem.services.UserService;
import com.tesfayedev.InventoryMgtSystem.specifications.TransactionFilter;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response purchase(TransactionRequest transactionRequest) {

        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("Product not found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new NotFoundException("Supplier not found"));

        User user = userService.getCurrentLoggedInUser();

        //save the product
        product.setStockQuantity(product.getStockQuantity()+quantity);
        productRepository.save(product);

        //create a transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.PURCHASE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .product(product)
                .user(user)
                .supplier(supplier)
                .totalProducts(quantity)
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        transactionRepository.save(transaction);
        return Response.builder()
                .status(200)
                .message("Purchase was successful")
                .build();

    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {

        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("Product not found"));

        User user = userService.getCurrentLoggedInUser();

        //update the stock quantity and re-save
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        //create a transaction
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.SALE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .product(product)
                .user(user)
                .totalProducts(quantity)
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

        transactionRepository.save(transaction);
        return Response.builder()
                .status(200)
                .message("Sale was successful")
                .build();

    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {

        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("Product not found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(()-> new NotFoundException("Supplier not found"));

        User user = userService.getCurrentLoggedInUser();

        //save the product
        product.setStockQuantity(product.getStockQuantity()-quantity);
        productRepository.save(product);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                .transactionStatus(TransactionStatus.PROCESSING)
                .product(product)
                .user(user)
                .totalProducts(quantity)
                .totalPrice(BigDecimal.ZERO)
                .description(transactionRequest.getDescription())
                .note(transactionRequest.getNote())
                .build();

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
                .orElseThrow(()-> new NotFoundException("Transactin Not FounD"));

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
}

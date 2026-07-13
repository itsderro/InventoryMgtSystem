package com.tesfayedev.InventoryMgtSystem.models;

import com.tesfayedev.InventoryMgtSystem.enums.PriceType;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionStatus;
import com.tesfayedev.InventoryMgtSystem.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
@Data
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalProducts;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name="price_type", nullable = false)
    private PriceType priceType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private String customerName;

    private String description;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionItem> items = new ArrayList<>();

    private String note;

    private final LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id")
    private Supplier supplier;

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", totalProducts=" + totalProducts +
                ", totalPrice=" + totalPrice +
                ", transactionType=" + transactionType +
                ", priceType=" + priceType +
                ", transactionStatus=" + transactionStatus +
                ", customerName='" + customerName + '\'' +
                ", description='" + description + '\'' +
                ", items=" + items +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", product=" + product +
                ", user=" + user +
                ", supplier=" + supplier +
                '}';
    }
}

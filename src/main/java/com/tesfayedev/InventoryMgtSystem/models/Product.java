package com.tesfayedev.InventoryMgtSystem.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tesfayedev.InventoryMgtSystem.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Data
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    @Column(unique = true)
    @NotNull(message = "Sku is required")
    private String sku;

    @Positive(message = "Cost price must be a positive value")
    private BigDecimal costPrice;

    @Positive(message = "Product price must be a positive value")
    private BigDecimal wholeSalePrice;

    @Positive(message = "Product price must be a positive value")
    private BigDecimal retailPrice;

    @Column(name = "wholesale_min_qty")
    private Integer wholesaleMinQty;

    @Min(value=0,message ="Stock quantity cannot be negative")
    private Integer stockQuantity;

    private String description;

    private LocalDateTime expiryDate;

    private String imageUrl;

    private final LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", wholeSalePrice=" + wholeSalePrice +
                ", retailPrice=" + retailPrice +
                ", stockQuantity=" + stockQuantity +
                ", description='" + description + '\'' +
                ", expiryDate=" + expiryDate +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", category=" + category +
                '}';
    }
}

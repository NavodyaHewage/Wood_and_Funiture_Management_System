package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Integer stockId;

    @OneToOne
    @JoinColumn(name = "product_cat_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "available_quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal availableQuantity;
}

package com.group_project.wfms_backend.model;

import com.group_project.wfms_backend.model.CustomerOrder;
import com.group_project.wfms_backend.model.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Customer_Order_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Details_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Order_Id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Product_Cat_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "Name", length = 200)
    private String name;

    @Column(name = "Quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "Price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "Line_Total", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal lineTotal;
}
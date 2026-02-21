package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="Quotation_Details")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class QuotationDeatails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Details_id")
    private Integer detailsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Quotation_Id", nullable = false)
    private Quatation quotation;

    @Column(name = "Order_Id")
    private Integer orderId;

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


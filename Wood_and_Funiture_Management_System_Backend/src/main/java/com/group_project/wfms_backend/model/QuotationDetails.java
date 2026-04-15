package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Quotation_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Details_id")
    private Integer detailsId;

    @ManyToOne
    @JoinColumn(name = "Quotation_Id", nullable = false)
    private Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "Product_Cat_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "Name")
    private String name;

    @Column(name = "Quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "Line_Total", insertable = false, updatable = false)
    private BigDecimal lineTotal;
}



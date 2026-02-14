package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

import java.math.BigDecimal;

@Entity
@Table(name="Customer_Order_Details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomerOrderDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name ="Customer_Order_ID")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id",nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name ="product_cat_id",nullable = false)
    private  ProductCategory productCategory;

    @Column(name="Name", length=200)
    private String name;

    @Column(name="Quantity",precision=10,scale=2,nullable = false)
    private BigDecimal quantity;

    @Column(name="price",precision=10,scale=2,nullable = false)
    private BigDecimal price;

    @Column(name="line_Total",precision = 15,scale=2,insertable = false,updatable = false)
    private BigDecimal lineTotal;






}

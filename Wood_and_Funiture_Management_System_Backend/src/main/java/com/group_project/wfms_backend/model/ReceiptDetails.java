package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="receipt_Details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Receipt_Details_Id")
    private Integer receiptDetailsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Receipt_Id", nullable = false)
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_Order_Details_Id", nullable = false)
    private CustomerOrderDetails customerOrderDetails;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;




}

package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Receipt_Id")
    private Long receiptId;

    @Column(name = "Receipt_Number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "Payment_Method")
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_Id", nullable = false)
    private Customer customer;

    @Column(name = "Total_Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;


    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;


    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptDetails> receiptDetails;

//   @OneToOne(mappedBy = "receipt", cascade = CascadeType.ALL)
//   private IncomeAccount incomeAccount;









}

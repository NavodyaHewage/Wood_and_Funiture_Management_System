package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="Quotation")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Quatation {


    @Id
    @GeneratedValue
    @Column(name="Quotation_Id")
    private Long quotationId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_Id", nullable = false)
    private Customer customerid;

    @Column(name = "Total_Amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private QuotationStatus status = QuotationStatus.PENDING;

    @Column(name = "Quotation_Date", nullable = false)
    private LocalDate quotationDate;

    @Column(name = "Valid_Until")
    private LocalDate validUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;

//    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<QuotationDetails> quotationDetails;


}

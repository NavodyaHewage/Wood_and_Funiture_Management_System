package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Quotation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Quotation_Id")
    private Integer quotationId;

    @ManyToOne
    @JoinColumn(name = "Customer_Id", nullable = false)
    private Customer customer;

    @Column(name = "Total_Amount")
    private BigDecimal totalAmount;

    @Column(name = "Status")
    private QuotationStatus status;

    @Column(name = "Quotation_Date", nullable = false)
    private LocalDate quotationDate;

    @Column(name = "Valid_Until")
    private LocalDate validUntil;

    @ManyToOne
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @Column(name = "Remarks")
    private String remarks;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationDetails> details;

    public Integer getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Integer quotationId) {
        this.quotationId = quotationId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
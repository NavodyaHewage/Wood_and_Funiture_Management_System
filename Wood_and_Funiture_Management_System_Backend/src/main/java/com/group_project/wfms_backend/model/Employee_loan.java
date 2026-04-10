package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="Employee_loan")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Employee_loan {
    @Id
    @Column(name ="Loan_id")
    private Integer loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Employee_id", nullable = false)
    private Employee employee;

    @Column(name = "Loan_Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "Issued_Date", nullable = false)
    private LocalDate issuedDate;

    @Column(name = "Reason", length = 255)
    private String reason;

    @Column(name = "Total_Deducted", precision = 15, scale = 2)
    private BigDecimal totalDeducted = BigDecimal.ZERO;

    // Generated column: Loan_Amount - Total_Deducted (read-only, managed by DB)
    @Column(name = "Balance", insertable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private LoanStatus status = LoanStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;

   




}

package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Employee_Loan_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoanDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Details_id")
    private Integer detailsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Loan_id", nullable = false)
    private Employee_loan loan;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // This links to the payroll execution that generated this deduction
    @Column(name = "Salary_details_id")
    private Integer salaryDetailsId;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;
}

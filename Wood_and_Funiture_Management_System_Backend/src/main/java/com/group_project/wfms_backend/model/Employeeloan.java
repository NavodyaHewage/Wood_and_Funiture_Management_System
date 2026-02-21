package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="Employee_Loan")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Employeeloan {
    @Id
    @Column(name ="Loan_id")
    private Integer loanId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Employee_id", nullable = false)
    private Employee employee;

    @Column(name = "Loan_Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Loan_Type")
    private LoanType loanType = LoanType.ADVANCE;

    @Column(name = "Taken_Date", nullable = false)
    private LocalDate takenDate;

    @Column(name = "Deduction_Amount", precision = 15, scale = 2)
    private BigDecimal deductionAmount = BigDecimal.ZERO;

    @Column(name = "Number_Of_Months", nullable = false)
    private Integer numberOfMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Salary_details_id")
    private EmployeeSalaryDetails salaryDetails;





}

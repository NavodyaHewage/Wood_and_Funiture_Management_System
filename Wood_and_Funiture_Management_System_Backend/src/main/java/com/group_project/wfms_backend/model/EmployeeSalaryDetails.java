package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Employee_Salary_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalaryDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Salary_Details_Id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Employee_Id", nullable = false)
    private Employee employee;

    @Column(name = "Basic_Salary", precision = 15, scale = 2, nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "Allowances", precision = 15, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "EPF_Amount", precision = 15, scale = 2)
    private BigDecimal epfAmount = BigDecimal.ZERO;

    @Column(name = "ETF_Amount", precision = 15, scale = 2)
    private BigDecimal etfAmount = BigDecimal.ZERO;

    @Column(name = "Net_Salary", precision = 15, scale = 2, nullable = false)
    private BigDecimal netSalary;

    @Column(name = "Effective_Date", nullable = false)
    private LocalDate effectiveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private Salary_details_Status status = Salary_details_Status.PENDING;

    @Column(name = "Is_Active")
    private Boolean isActive = true;
}

package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Employee_Paysheet")
@Data
public class EmployeePaysheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Paysheet_id")
    private Integer paysheetId;

    @ManyToOne
    @JoinColumn(name = "Employee_id")
    private Employee employee;

    @Column(name = "Month")
    private Integer month;

    @Column(name = "Year")
    private Integer year;

    @Column(name = "Present_Days")
    private Integer presentDays;

    @Column(name = "Base_Salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "Overtime_Amount", precision = 15, scale = 2)
    private BigDecimal overtimeAmount;

    @Column(name = "Loan_Deduction", precision = 15, scale = 2)
    private BigDecimal loanDeduction;

    @Column(name = "Other_Deduction", precision = 15, scale = 2)
    private BigDecimal otherDeduction;

    @Column(name = "Total_Earnings", precision = 15, scale = 2)
    private BigDecimal totalEarnings;

    @Column(name = "Net_Salary", precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "Generated_Date")
    private LocalDateTime generatedDate = LocalDateTime.now();

    @Column(name = "File_Path")
    private String filePath;
}

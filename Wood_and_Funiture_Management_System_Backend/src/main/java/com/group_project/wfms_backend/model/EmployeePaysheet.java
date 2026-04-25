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
    private Integer paysheetId;

    @ManyToOne
    @JoinColumn(name = "Employee_id")
    private Employee employee;

    private Integer month;
    private Integer year;
    private Integer presentDays;

    private BigDecimal baseSalary;
    private BigDecimal overtimeAmount;
    private BigDecimal loanDeduction;
    private BigDecimal otherDeduction;
    private BigDecimal totalEarnings;
    private BigDecimal netSalary;

    private LocalDateTime generatedDate = LocalDateTime.now();
    private String filePath;
}

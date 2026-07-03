package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaySheetDTO {
    private String employeeName;
    private String designation;
    private Integer month;
    private Integer year;
    private LocalDate date;
    private Integer presentDays;
    private BigDecimal baseSalary;
    private BigDecimal overtimeAmount;
    private BigDecimal loanDeduction;
    private BigDecimal otherDeduction;
    private BigDecimal totalEarnings;
    private BigDecimal deductions;
    private BigDecimal netSalary;

}

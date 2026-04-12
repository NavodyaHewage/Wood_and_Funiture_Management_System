package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaySheetDTO {
    private String employeeName;
    private String designation;
    private Integer month;
    private Integer year;
    private Integer presentDays;
    private BigDecimal baseSalary;
    private BigDecimal totalEarnings;
    private BigDecimal deductions;
    private BigDecimal netSalary;

}

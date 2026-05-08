package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PayrollResponseDTO {
    private String employeeName;
    private String designation;
    private BigDecimal baseSalary;
    private Double overtimeHours;
    private BigDecimal overtimeAmount;
    private BigDecimal loanDeduction;
    private BigDecimal otherDeduction;
    private BigDecimal previouslyPaidAmount; // Sum of daily wages issued
    private BigDecimal netSalary;
    private List<String> attendanceWarnings;
}

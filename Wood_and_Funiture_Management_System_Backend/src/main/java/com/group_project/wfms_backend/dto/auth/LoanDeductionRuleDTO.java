package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanDeductionRuleDTO {
    private Integer ruleId;
    private Integer loanId; // Reference by ID
    private BigDecimal deductionAmount;
    private Integer startMonth;
    private Integer startYear;
    private Integer endMonth;
    private Integer endYear;
    private Boolean isActive;
    private Integer createdById;
    private String remarks;
}

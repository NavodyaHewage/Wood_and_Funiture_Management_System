package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayrollRequestDTO {
    private Integer employeeId;
    private Integer month;
    private Integer year;
    private BigDecimal otherDeduction;
    private String otherDeductionReason;
    private BigDecimal loanDeductionOverride;
    private String paymentType; // "DAILY" or "MONTHLY"
    private Boolean isLoanDeductionEnabled;
    // Exact date to calculate/generate a DAILY paysheet for. Defaults to today when absent.
    private LocalDate date;

    public Boolean isLoanDeductionEnabled() {
        return isLoanDeductionEnabled;
    }
}

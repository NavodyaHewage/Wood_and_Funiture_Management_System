package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeLoanDTO {
    private Integer loanId;
    private Integer employeeId; // Reference by ID
    private String employeeName; // For display convenience
    private BigDecimal loanAmount;
    private LocalDate issuedDate;
    private String reason;
    private BigDecimal totalDeducted;
    private BigDecimal balance; // Read-only in backend
    private String status;
    private Integer createdById;
    private String remarks;
}

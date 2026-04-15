package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeAccountDTO {
    private Integer incomeId;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private Long receiptId;
    private Integer createdById;

}

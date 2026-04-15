package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseAccountDTO {

    private Integer expenseId;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private String paidTo;
    private String remarks;
    private Integer expenseTypeId;
    private Integer grnId; // මෙය Null විය හැක
    private Integer userId;
}

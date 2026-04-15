package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

@Data
public class ExpenseTypeDTO {
    private Integer expenseTypeId;
    private String typeName;
    private String description;
}

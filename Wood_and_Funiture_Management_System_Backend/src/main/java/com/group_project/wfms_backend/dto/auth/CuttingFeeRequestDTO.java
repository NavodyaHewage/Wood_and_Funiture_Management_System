package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CuttingFeeRequestDTO {
    private Integer supplyId;
    private Integer employeeId;
    private BigDecimal fee;
    private LocalDate date;
    private String remarks;
}
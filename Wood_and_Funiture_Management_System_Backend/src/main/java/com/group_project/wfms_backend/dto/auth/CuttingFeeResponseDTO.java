package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CuttingFeeResponseDTO {
    private Integer id;
    private Integer supplyId;
    private String invoiceNumber;
    private String supplierName;
    private Integer employeeId;
    private String employeeName;
    private BigDecimal fee;
    private LocalDate date;
    private String remarks;
}
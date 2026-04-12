package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GrnDetailResponseDTO {
    private Integer id;
    private Integer supplyRawMaterialDetailsId;
    private Integer logNumber;
    private BigDecimal lengthFt;
    private BigDecimal girthFt;
    private BigDecimal totalQuantityCft;
    private String grnNumber;
    private LocalDate date;
    private BigDecimal amount;
}
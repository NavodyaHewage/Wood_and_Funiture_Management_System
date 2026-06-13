package com.group_project.wfms_backend.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialStatusDTO {
    private String rawMaterialName;
    private String status;
    private BigDecimal quantity;
    private BigDecimal costPerUnit;
    private BigDecimal totalStockValue;
}

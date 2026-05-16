package com.group_project.wfms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRawMaterialDTO {
    private Integer id;
    private String invoiceNumber;
    private Integer logNumber;
    private BigDecimal lengthFt;
    private BigDecimal girthFt;
    private BigDecimal totalQuantityCft;
    private BigDecimal price;
    private String status;
}

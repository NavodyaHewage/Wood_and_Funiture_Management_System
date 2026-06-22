package com.group_project.wfms_backend.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockStatusDTO {
    private String productName;
    private String productCategory;
    private BigDecimal availableQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalStockValue;
}

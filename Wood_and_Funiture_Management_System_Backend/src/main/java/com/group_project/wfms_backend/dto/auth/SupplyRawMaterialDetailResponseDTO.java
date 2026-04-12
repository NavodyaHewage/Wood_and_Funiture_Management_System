package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplyRawMaterialDetailResponseDTO {

    private Integer id;
    private Integer rmId;
    private String rmName;
    private Integer logNumber;
    private BigDecimal lengthFt;
    private BigDecimal girthFt;
    private BigDecimal totalQuantityCft;   // generated column value
    private BigDecimal price;
    private BigDecimal lineTotal;          // generated column value
}

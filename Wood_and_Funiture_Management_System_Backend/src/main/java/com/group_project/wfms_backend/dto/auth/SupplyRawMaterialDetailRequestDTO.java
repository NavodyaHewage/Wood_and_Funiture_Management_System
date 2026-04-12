package com.group_project.wfms_backend.dto.auth;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplyRawMaterialDetailRequestDTO {

    private Integer rmId;
    private Integer logNumber;
    private BigDecimal lengthFt;
    private BigDecimal girthFt;
    private BigDecimal price;
}
package com.group_project.wfms_backend.dto.auth;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class RawMaterialItemRequestDTO {
    private String rmName;
    private BigDecimal pricePerCft;
    private String description;
}
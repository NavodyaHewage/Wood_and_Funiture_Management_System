package com.group_project.wfms_backend.dto.auth;



import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GrnDetailRequestDTO {
    private Integer supplyRawMaterialDetailsId;
    private String grnNumber;
    private LocalDate date;
    private BigDecimal amount;
}
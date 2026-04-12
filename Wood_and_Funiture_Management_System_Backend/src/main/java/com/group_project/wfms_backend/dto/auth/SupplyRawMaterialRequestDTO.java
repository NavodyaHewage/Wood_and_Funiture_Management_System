package com.group_project.wfms_backend.dto.auth;




import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SupplyRawMaterialRequestDTO {

    private Integer supplierId;
    private Integer rmId;
    private String invoiceNumber;
    private BigDecimal transport;
    private LocalDate supplyDate;
    private Integer createdById;
    private List<SupplyRawMaterialDetailRequestDTO> supplyDetails;
}
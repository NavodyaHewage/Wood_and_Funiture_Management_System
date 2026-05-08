package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SupplyRawMaterialResponseDTO {

    private Integer supplyId;
    private Integer supplierId;
    private String supplierName;
    private Integer rmId;
    private String rmName;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private BigDecimal transport;
    private BigDecimal netAmount;
    private Boolean isTreeSeller;
    private LocalDate supplyDate;
    private List<SupplyRawMaterialDetailResponseDTO> supplyDetails;
}
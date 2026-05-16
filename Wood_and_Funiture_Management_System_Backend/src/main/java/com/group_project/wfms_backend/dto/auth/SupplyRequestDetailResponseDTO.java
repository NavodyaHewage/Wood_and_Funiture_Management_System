package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplyRequestDetailResponseDTO {
    private Integer requestDetailId;
    private Integer rmId;
    private String rmName;
    private BigDecimal adminRequestedCft;
    private BigDecimal supplierApprovedCft;
    private BigDecimal unitPrice;
    private String remarks;
}

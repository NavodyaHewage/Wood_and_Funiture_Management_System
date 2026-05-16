package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplyRequestDetailDTO {
    private Integer rmId;
    private BigDecimal adminRequestedCft;
    private BigDecimal supplierApprovedCft;
    private BigDecimal unitPrice;
    private String remarks;
}

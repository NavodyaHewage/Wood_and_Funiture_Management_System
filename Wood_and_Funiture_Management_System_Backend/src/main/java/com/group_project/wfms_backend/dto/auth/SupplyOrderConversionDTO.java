package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SupplyOrderConversionDTO {
    private String invoiceNumber;
    private BigDecimal transport;
    private BigDecimal cuttingFee;
    private Integer cuttingFeeEmployeeId;
    private LocalDate supplyDate;
}

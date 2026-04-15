package com.group_project.wfms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDetailDTO {

    private Long customerOrderDetailsId;
    private BigDecimal amount;
    private Long receiptDetailsId;
    private String productName;

}

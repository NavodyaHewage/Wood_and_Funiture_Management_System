package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceiptDetailsDTO {
        private Integer receiptDetailsId;
        private Long receiptId;
        private Integer customerOrderDetailsId;
        private BigDecimal amount;
}

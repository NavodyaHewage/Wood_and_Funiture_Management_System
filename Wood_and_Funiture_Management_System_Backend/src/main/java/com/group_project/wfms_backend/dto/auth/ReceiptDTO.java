package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReceiptDTO {
    private Long receiptId;
    private String receiptNumber;
    private LocalDate date;
    private String paymentMethod;
    private Integer customerId;
    private BigDecimal totalAmount;
    private Integer createdById;
    private String remarks;
    private List<ReceiptDetailsDTO> details; // පේළි කිහිපයක් එකවර ඇතුළත් කිරීමට
}

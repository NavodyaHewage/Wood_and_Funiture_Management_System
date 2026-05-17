package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReceiptResponseDTO {
    private Long receiptId;
    private String receiptNumber;
    private LocalDate date;
    private String paymentMethod;
    private Integer customerId;
    private String customerName;
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private Long createdById;
    private String remarks;
    private String chequeNumber;
    private String bankName;
    private String cardType;
    private String cardLastDigits;
    private List<ReceiptDetailDTO> receiptDetails;

}
package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReceiptRequestDTO {
    private String receiptNumber;
    private LocalDate date;
    private String paymentMethod;   // "Cash", "Card", "Bank_Transfer", "Cheque"
    private Integer customerId;
    private Long orderId;
    private BigDecimal totalAmount;
    private Integer createdById;
    private String remarks;
    private String chequeNumber;
    private String bankName;
    private String cardType;
    private String cardLastDigits;
    private List<ReceiptDetailDTO> receiptDetails;

}
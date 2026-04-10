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
    private BigDecimal totalAmount;
    private  Integer createdById;
    private String remarks;
    private List<ReceiptDetailDTO> receiptDetails;

//    @Data
//    public static class ReceiptDetailDTO {
//        private Long customerOrderDetailsId;
//        private BigDecimal amount;
//    }
}
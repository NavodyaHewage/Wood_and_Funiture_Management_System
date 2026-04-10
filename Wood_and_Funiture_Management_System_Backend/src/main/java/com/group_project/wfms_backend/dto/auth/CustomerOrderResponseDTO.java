package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerOrderResponseDTO {
    private Long orderId;
    private Integer customerId;
    private String customerName;
    private String receiptNumber;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String status;
    private LocalDate orderDate;
    private List<OrderDetailDTO> orderDetails;

    @Data
    public static class OrderDetailDTO {
        private Long detailId;
        private Integer productCatId;
        private String productCatName;
        private String name;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal lineTotal;
    }
}
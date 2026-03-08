package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerOrderRequestDTO {
    private Integer customerId;
    private String receiptNumber;
    private BigDecimal paidAmount;
    private String status;           // Optional override
    private LocalDate orderDate;
    private Long createdById;
    private List<OrderDetailDTO> orderDetails;

    @Data
    public static class OrderDetailDTO {
        private Integer productCatId;
        private String name;
        private BigDecimal quantity;
        private BigDecimal price;
    }
}

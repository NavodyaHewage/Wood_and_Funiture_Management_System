package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OutstandingLinesResponseDTO {
    private OrderSummary order;
    private List<OutstandingLine> lines;

    @Data
    public static class OrderSummary {
        private Long orderId;
        private String orderNumber;
        private BigDecimal balanceAmount;
    }

    @Data
    public static class OutstandingLine {
        private Long detailId;
        private Integer productCatId;
        private String name;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal lineTotal;
        private BigDecimal paidAmount;
        private BigDecimal outstanding;
    }
}

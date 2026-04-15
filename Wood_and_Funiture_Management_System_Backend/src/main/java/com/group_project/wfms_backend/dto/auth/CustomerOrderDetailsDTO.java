package com.group_project.wfms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class CustomerOrderDetailsDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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


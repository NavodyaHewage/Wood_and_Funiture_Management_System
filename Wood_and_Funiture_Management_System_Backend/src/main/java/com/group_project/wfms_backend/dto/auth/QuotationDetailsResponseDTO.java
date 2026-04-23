package com.group_project.wfms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDetailsResponseDTO {
    private Integer detailsId;
    private Integer quotationId;
    private Integer orderId;
    private Integer productCatId;
    private String productCatName;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;
}

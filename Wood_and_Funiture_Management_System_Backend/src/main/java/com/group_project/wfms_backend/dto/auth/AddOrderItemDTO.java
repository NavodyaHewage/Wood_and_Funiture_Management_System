package com.group_project.wfms_backend.dto.auth;

//order ekata adala details
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderItemDTO {

        private Integer productCatId;
        private BigDecimal quantity;
        private BigDecimal price;

}

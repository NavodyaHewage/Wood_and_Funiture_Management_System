package com.group_project.wfms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialItemResponseDTO {
    private Integer rmId;
    private String rmName;
    private BigDecimal pricePerCft;
    private String description;
}

package com.group_project.wfms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CutProductDTO {
    private Integer productCategoryId;
    private BigDecimal quantity;
    private String notes;
}

package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Setter
@Getter

public class AssetAccountDTO {
    private Integer assetId;
    private String assetName;
    private String assetType; // String ලෙස Enum එක හසුරුවයි
    private LocalDate purchaseDate;
    private BigDecimal purchaseValue;
    private BigDecimal currentValue;
    private BigDecimal depreciationRate;
    private String status;
    private String description;
    private Integer userId; // CreatedBy ගේ ID එක
}

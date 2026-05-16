package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.util.List;

@Data
public class SupplyRequestDTO {
    private Integer supplierId;
    private Boolean transportBySupplier;
    private String transportNotes;
    private String remarks;
    private Integer createdBy;
    private List<SupplyRequestDetailDTO> details;
}

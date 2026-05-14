package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupplyRequestResponseDTO {
    private Integer requestId;
    private Integer supplierId;
    private String supplierName;
    private LocalDateTime requestDate;
    private String status;
    private Boolean transportBySupplier;
    private String transportNotes;
    private String remarks;
    private Integer createdById;
    private String createdByUsername;
    private LocalDateTime approvedDate;
    private List<SupplyRequestDetailResponseDTO> details;
}

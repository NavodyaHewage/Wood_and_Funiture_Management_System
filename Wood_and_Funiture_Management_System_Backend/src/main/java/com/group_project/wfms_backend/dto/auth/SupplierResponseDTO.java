package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupplierResponseDTO {
    private Long supId;
    private String supName;
    private String supCat;
    private String mobile;
    private String address;
    private String email;
    private LocalDateTime createdDate;
    private Boolean isActive;
}
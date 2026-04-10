package com.group_project.wfms_backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequestDTO {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 200)
    private String supName;

    @Size(max = 100)
    private String supCat;

    @NotBlank(message = "Mobile number is required")
    @Size(max = 15)
    private String mobile;

    private String address;

    @Size(max = 100)
    private String email;

    private Boolean isActive = true;
}
package com.group_project.wfms_backend.dto.auth;

import com.group_project.wfms_backend.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Username is required for system users")
    private String username;

    @NotBlank(message = "Password is required for system users")
    private String password;

    @Email(message = "Email should be valid")
    private String email;

    private String phoneNumber; // Map to mobile for entities

    private UserRole role;

    private String userDetails;

    // Redesign logic fields
    private Boolean isSystemUser;
    private String entityType; // EMPLOYEE, SUPPLIER, CUSTOMER

    // Entity Specific Fields
    private String fullName; // Employee/Customer/Supplier name
    private String nic;
    private String address;
    private String mobile; // Redundant with phoneNumber, but aligning with entities
    private String designation; // Employee
    private String dateJoined; // Employee
    private String supCat; // Supplier
}

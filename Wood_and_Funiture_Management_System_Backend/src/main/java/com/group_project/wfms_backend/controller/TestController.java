package com.group_project.wfms_backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public String publicAccess() {
        return "Public Content - No authentication required";
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public String userAccess() {
        return "User Content - Any authenticated user can access";
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String employeeAccess() {
        return "Employee Content - Only EMPLOYEE role can access";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Content - Only ADMIN role can access";
    }

    @GetMapping("/employee-or-admin")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public String employeeOrAdminAccess() {
        return "Employee or Admin Content - EMPLOYEE or ADMIN roles can access";
    }

    @GetMapping("/all-roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'SUPPLIER')")
    public String allRolesAccess() {
        return "All Roles Content - ADMIN, EMPLOYEE, or SUPPLIER can access";
    }
}

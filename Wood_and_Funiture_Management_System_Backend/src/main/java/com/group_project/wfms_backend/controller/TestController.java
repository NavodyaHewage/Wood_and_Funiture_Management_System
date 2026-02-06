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

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerAccess() {
        return "Manager Content - Only MANAGER role can access";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Content - Only ADMIN role can access";
    }

    @GetMapping("/manager-or-admin")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String managerOrAdminAccess() {
        return "Manager or Admin Content - MANAGER or ADMIN roles can access";
    }

    @GetMapping("/all-roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPPLIER')")
    public String allRolesAccess() {
        return "All Roles Content - ADMIN, MANAGER, or SUPPLIER can access";
    }
}

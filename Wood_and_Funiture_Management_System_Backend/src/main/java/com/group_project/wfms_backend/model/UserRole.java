package com.group_project.wfms_backend.model;

public enum UserRole {
    ADMIN("Admin"),
    SUPPLIER("Supplier"),
    MANAGER("Manager");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
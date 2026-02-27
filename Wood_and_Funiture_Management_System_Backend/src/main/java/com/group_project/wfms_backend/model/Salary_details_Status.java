package com.group_project.wfms_backend.model;

public enum Salary_details_Status {
    PENDING("Pending") ,
    PARTIALLY_PAID("Partialy Paid"),
            PAID("Paid");
    private final String displayName;
    Salary_details_Status(String displayName) {
        this.displayName = displayName;

    }
    public String getDisplayName() {
        return displayName;

    }
}

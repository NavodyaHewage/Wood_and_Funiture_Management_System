package com.group_project.wfms_backend.model;

public enum PaysheetStatus {
    DRAFT("Draft"),
    FINALIZED("Finalized"),
    APPROVED("Approved"),
    PAID("Paid");
    private String displayName;
    PaysheetStatus(String displayName) {
        this.displayName = displayName;

    }
    public String getDisplayName() {
        return displayName;

    }

}

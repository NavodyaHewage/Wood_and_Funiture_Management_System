package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum LoanStatus {
    ACTIVE("Active"),
    PARTIALLY_PAID("Partially Paid"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

}

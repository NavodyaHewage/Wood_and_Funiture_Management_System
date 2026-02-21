package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum LoanType {
    LOAN("Loan"),
    ADVANCE("Advance"),
    OTHER("Other");

    private final String displayName;

    LoanType(String displayName) {
        this.displayName = displayName;
    }

}

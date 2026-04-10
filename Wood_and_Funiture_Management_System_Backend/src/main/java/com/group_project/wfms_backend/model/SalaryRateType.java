package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum SalaryRateType {
    DAILY("Daily"),
    MONTHLY("Monthly"),
    HOURLY("Hourly"),
    PER_UNIT("Per Unit");
    private final String displayName;
    SalaryRateType(String displayName) {
        this.displayName = displayName;
    }
}

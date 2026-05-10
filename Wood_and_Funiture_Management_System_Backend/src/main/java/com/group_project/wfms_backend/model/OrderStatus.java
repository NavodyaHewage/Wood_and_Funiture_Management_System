package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    @com.fasterxml.jackson.annotation.JsonValue
    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static OrderStatus fromString(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(value) || status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
// enum class eke just eka define krnwanm thmi mehema danne habayi api eka extra infomation danwanm api ekata kranne thwath code krla meke dana eka
//Extra info = anything you want to attach to the enum value (like a description, display name, code, number, etc.)
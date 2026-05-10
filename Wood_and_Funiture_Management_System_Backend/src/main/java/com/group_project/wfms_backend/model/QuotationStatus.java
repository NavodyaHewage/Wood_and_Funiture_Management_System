package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum QuotationStatus {
    PENDING("Pending"),       // Awaiting customer response
    APPROVED("Approved"),     // Customer approved the quotation
    REJECTED("Rejected"),   // Customer rejected the quotation
    READY_TO_ORDER("Ready to Order"), // Stock checked and ready
    CONVERTED("Converted");   // Converted to an order

    @com.fasterxml.jackson.annotation.JsonValue
    private final String displayName;

    QuotationStatus(String displayName) {
        this.displayName = displayName;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static QuotationStatus fromString(String value) {
        for (QuotationStatus status : QuotationStatus.values()) {
            if (status.name().equalsIgnoreCase(value) || status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }

}

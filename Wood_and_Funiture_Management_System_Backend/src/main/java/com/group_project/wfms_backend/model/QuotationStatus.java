package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum QuotationStatus {
    PENDING("Pending"),       // Awaiting customer response
    APPROVED("Approved"),     // Customer approved the quotation
    REJECTED("Rejected"),   // Customer rejected the quotation
    CONVERTED("Converted");   // Converted to an order

    private final String displayName;

    QuotationStatus(String displayName) {
        this.displayName = displayName;
    }

}

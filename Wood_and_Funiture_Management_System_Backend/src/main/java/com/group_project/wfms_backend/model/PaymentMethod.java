package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum PaymentMethod {
        CASH("Cash"),
        BANK_TRANSFER("Bank Transfer"),
        CHEQUE("Cheque");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

}

package com.group_project.wfms_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        // Normalize string: uppercase, replace spaces with underscores (e.g. "Bank Transfer" -> "BANK_TRANSFER")
        String normalized = dbData.trim().toUpperCase().replace(" ", "_");

        for (PaymentMethod val : PaymentMethod.values()) {
            if (val.name().equals(normalized)) {
                return val;
            }
        }

        // Fallback: check case-insensitive match against displayName
        for (PaymentMethod val : PaymentMethod.values()) {
            if (val.getDisplayName().equalsIgnoreCase(dbData.trim())) {
                return val;
            }
        }

        throw new IllegalArgumentException("Unknown database value for PaymentMethod: " + dbData);
    }
}

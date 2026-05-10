package com.group_project.wfms_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QuotationStatusConverter implements AttributeConverter<QuotationStatus, String> {

    @Override
    public String convertToDatabaseColumn(QuotationStatus status) {
        if (status == null) {
            return null;
        }
        return status.getDisplayName(); // Store as Pending, Approved, Converted, etc.
    }

    @Override
    public QuotationStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return QuotationStatus.fromString(dbData);
    }
}

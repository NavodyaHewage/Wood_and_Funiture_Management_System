package com.group_project.wfms_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AttendanceStatusConverter implements AttributeConverter<AttendanceStatus, String> {

    @Override
    public String convertToDatabaseColumn(AttendanceStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public AttendanceStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Handle case variations from the DB gracefully
        for (AttendanceStatus status : AttendanceStatus.values()) {
            if (status.name().equalsIgnoreCase(dbData) || status.getDisplayName().equalsIgnoreCase(dbData)) {
                return status;
            }
        }
        // Fallback to strict valueOf which might throw IllegalArgumentException
        return AttendanceStatus.valueOf(dbData.toUpperCase().replace(" ", "_"));
    }
}

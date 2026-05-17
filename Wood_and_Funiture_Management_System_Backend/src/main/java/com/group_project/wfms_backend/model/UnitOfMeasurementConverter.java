package com.group_project.wfms_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UnitOfMeasurementConverter implements AttributeConverter<UnitOfMeasurement, String> {

    @Override
    public String convertToDatabaseColumn(UnitOfMeasurement unit) {
        if (unit == null) {
            return null;
        }
        return unit.getDisplayName();
    }

    @Override
    public UnitOfMeasurement convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return UnitOfMeasurement.fromString(dbData);
    }
}

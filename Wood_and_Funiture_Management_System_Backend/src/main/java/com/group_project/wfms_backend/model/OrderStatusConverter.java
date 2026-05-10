package com.group_project.wfms_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return status.getDisplayName();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return OrderStatus.fromString(dbData);
    }
}

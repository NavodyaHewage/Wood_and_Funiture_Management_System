package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum UnitOfMeasurement {
    CUBIC_FEET("Cubic Feet"),
    SQUARE_FEET("Square Feet"),
    LENGTH_FEET("Length Feet"),
    PIECES("Pieces"),
    KG("Kg");

    @com.fasterxml.jackson.annotation.JsonValue
    private final String displayName;

    UnitOfMeasurement(String displayName) {
        this.displayName = displayName;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static UnitOfMeasurement fromString(String value) {
        for (UnitOfMeasurement unit : UnitOfMeasurement.values()) {
            if (unit.name().equalsIgnoreCase(value) || unit.displayName.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        return SQUARE_FEET;
    }
}

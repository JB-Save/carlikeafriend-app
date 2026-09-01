package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum InspectionType {
    PICKUP("Recogida"),
    RETURN("Devolución");

    private final String description;
    private static final Map<String, InspectionType> ENUM_MAP;

    InspectionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, InspectionType> map = new ConcurrentHashMap<>();
        for (InspectionType instance : InspectionType.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<InspectionType> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    public static InspectionType validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El estado '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }
}

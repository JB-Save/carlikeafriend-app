package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum InsuranceType {
    BASIC("Básico - Responsabilidad Civil"),
    PREMIUM("Premium - Daños Parciales"),
    FULL_COVERAGE("Cobertura Total - Sin Deducible");

    private final String description;
    private static final Map<String, InsuranceType> ENUM_MAP;

    InsuranceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, InsuranceType> map = new ConcurrentHashMap<>();
        for (InsuranceType instance : InsuranceType.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<InsuranceType> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    public static InsuranceType validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El tipo de seguro '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }
}

package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum ChargeType {
    PER_DAY("Por Día"),
    FLAT_FEE("Tarifa Fija");

    private final String description;
    private static final Map<String, ChargeType> ENUM_MAP;

    ChargeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, ChargeType> map = new ConcurrentHashMap<>();
        for (ChargeType instance : ChargeType.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<ChargeType> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    public static ChargeType validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El tipo de cargo '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }
}

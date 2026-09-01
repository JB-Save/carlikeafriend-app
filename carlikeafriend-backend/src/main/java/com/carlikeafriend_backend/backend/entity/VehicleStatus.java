package com.carlikeafriend_backend.backend.entity;


import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum VehicleStatus {
    AVAILABLE("Disponible"),
    RENTED("Alquilado"),
    MAINTENANCE("En Mantenimiento"),
    OUT_OF_SERVICE("Fuera de Servicio");


    private final String description;
    private static final Map<String, VehicleStatus> ENUM_MAP;

    VehicleStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, VehicleStatus> map = new ConcurrentHashMap<>();
        for (VehicleStatus instance : VehicleStatus.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<VehicleStatus> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    public static VehicleStatus validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El estado '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }

}

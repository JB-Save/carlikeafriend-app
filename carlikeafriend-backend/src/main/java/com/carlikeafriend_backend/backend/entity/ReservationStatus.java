package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum ReservationStatus {
    PENDING_CONFIRMATION("Pendiente de Confirmación"),
    CONFIRMED("Confirmada"),
    IN_PROGRESS("En Alquiler"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada");

    private final String description;
    private static final Map<String, ReservationStatus> ENUM_MAP;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, ReservationStatus> map = new ConcurrentHashMap<>();
        for (ReservationStatus instance : ReservationStatus.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Búsqueda optimizada O(1)
     */
    public static Optional<ReservationStatus> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    /**
     * Validación limpia para servicios
     */
    public static ReservationStatus validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El estado '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }
}

package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum PaymentStatus {
    PENDING("Pendiente de Pago"),
    AUTHORIZED("Autorizado"),
    PAID("Pagado"),
    REFUNDED("Reembolsado"),
    FAILED("Fallido");

    private final String description;
    private static final Map<String, PaymentStatus> ENUM_MAP;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, PaymentStatus> map = new ConcurrentHashMap<>();
        for (PaymentStatus instance : PaymentStatus.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<PaymentStatus> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    public static PaymentStatus validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El estado '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }
}

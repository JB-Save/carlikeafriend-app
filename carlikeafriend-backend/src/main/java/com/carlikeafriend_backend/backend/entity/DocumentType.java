package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.exception.InvalidResourceStateException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum DocumentType {
    CC("Cédula de Ciudadanía"),
    CE("Cédula de Extranjería"),
    PASSPORT("Pasaporte"),
    DNI("Documento Nacional de Identidad"),
    NIT("Número de Identificación Tributaria");

    private final String description;
    private static final Map<String, DocumentType> ENUM_MAP;

    DocumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Map<String, DocumentType> map = new ConcurrentHashMap<>();
        for (DocumentType instance : DocumentType.values()) {
            map.put(instance.name().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<DocumentType> fromString(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        return Optional.ofNullable(ENUM_MAP.get(value.trim().toLowerCase()));
    }

    public static DocumentType validate(String value) {
        return fromString(value)
                .orElseThrow(() -> new InvalidResourceStateException(
                        String.format("El tipo de documento '%s' no es válido. Opciones: %s",
                                value, ENUM_MAP.keySet())
                ));
    }
}

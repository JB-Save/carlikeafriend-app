package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PermissionDTO {

    @NotBlank(message = "El permiso no puede estar vacío")
    @Size(min = 6, max = 25, message = "El permiso debe tener entre 6 a 25 caracteres")
    private String name;
    @Size(min = 10, max = 100, message = "La descripción debe tener entre 10 a 100 caracteres")
    private String description;

    public PermissionDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

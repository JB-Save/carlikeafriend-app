package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MaintenanceTypeDTO {

    @NotBlank(message = "El código de mantenimiento es obligatorio")
    @Size(max = 10, message = "El código de mantenimiento no debe exceder los 10 caracteres")
    private String code;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String description;

    public MaintenanceTypeDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

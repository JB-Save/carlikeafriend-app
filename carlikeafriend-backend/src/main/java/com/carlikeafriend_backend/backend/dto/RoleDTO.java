package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class RoleDTO {

    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 25, message = "El rol no debe exceder los 25 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción no debe exceder los 100 caracteres")
    private String description;

    private Set<Long> permissionsIds;

    public RoleDTO() {
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

    public Set<Long> getPermissions() {
        return permissionsIds;
    }

    public void setPermissions(Set<Long> permissionsIds) {
        this.permissionsIds = permissionsIds;
    }
}

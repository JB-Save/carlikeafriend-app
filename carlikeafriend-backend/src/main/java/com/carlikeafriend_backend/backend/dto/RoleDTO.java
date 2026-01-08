package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class RoleDTO {

    @NotBlank(message = "El rol no puede estar vacío")
    @Size(min = 4, max = 25, message = "El rol debe tener entre 4 a 25 caracteres")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 10, max = 100, message = "La descripción debe tener entre 10 a 100 caracteres")
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

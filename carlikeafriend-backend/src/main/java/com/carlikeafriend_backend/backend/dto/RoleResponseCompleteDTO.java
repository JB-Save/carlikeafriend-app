package com.carlikeafriend_backend.backend.dto;


import java.util.List;

public class RoleResponseCompleteDTO {

    private Long id;
    private String name;
    private String description;
    private List<PermissionResponseDTO> permissions;

    public RoleResponseCompleteDTO() {
    }

    public RoleResponseCompleteDTO(Long id, String name, String description, List<PermissionResponseDTO> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<PermissionResponseDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionResponseDTO> permissions) {
        this.permissions = permissions;
    }
}

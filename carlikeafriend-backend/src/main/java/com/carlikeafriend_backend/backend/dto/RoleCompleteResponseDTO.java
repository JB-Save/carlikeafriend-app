package com.carlikeafriend_backend.backend.dto;


import java.util.List;

public class RoleCompleteResponseDTO {

    private Long id;
    private String name;
    private String description;
    private List<SimpleResponseDTO> permissions;

    public RoleCompleteResponseDTO() {
    }

    public RoleCompleteResponseDTO(Long id, String name, String description, List<SimpleResponseDTO> permissions) {
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

    public List<SimpleResponseDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SimpleResponseDTO> permissions) {
        this.permissions = permissions;
    }
}

package com.carlikeafriend_backend.backend.dto;

public class PermissionCompleteResponseDTO {

    private Long id;
    private String name;
    private String description;

    public PermissionCompleteResponseDTO() {
    }

    public PermissionCompleteResponseDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}

package com.carlikeafriend_backend.backend.dto;

import java.util.List;

public class UserResponseCompleteDTO {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private List<RoleResponseDTO> roles;

    public UserResponseCompleteDTO() {
    }

    public UserResponseCompleteDTO(Long id, String name, String lastName, String email, List<RoleResponseDTO> roles) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String userName) {
        this.email = email;
    }

    public List<RoleResponseDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResponseDTO> roles) {
        this.roles = roles;
    }
}

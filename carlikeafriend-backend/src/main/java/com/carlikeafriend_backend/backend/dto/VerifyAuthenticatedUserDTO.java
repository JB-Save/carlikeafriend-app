package com.carlikeafriend_backend.backend.dto;

import java.util.List;

public class VerifyAuthenticatedUserDTO {
    private Long id;
    private String name;
    private String lastName;
    private String userName;
    private List<SimpleResponseDTO> roles;

    public VerifyAuthenticatedUserDTO() {
    }

    public VerifyAuthenticatedUserDTO(Long id, String name, String lastName, String userName, List<SimpleResponseDTO> roles) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<SimpleResponseDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<SimpleResponseDTO> roles) {
        this.roles = roles;
    }
}

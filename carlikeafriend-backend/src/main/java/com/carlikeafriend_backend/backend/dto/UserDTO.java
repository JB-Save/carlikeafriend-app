package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {

    @NotBlank(message = "El Nombre no debe estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @NotBlank(message = "El apellido no debe estar vacío")
    @Size(min = 3, max = 100, message = "El apellido debe tener entre 3 y 100 caracteres")
    private String lastName;

    @NotBlank(message = "El correo electrónico no debe estar vacío")
    @Email(message = "El formato del email es inválido")
    @Size(max = 255, message = "El email no debe exceder los 255 caracteres")
    private String email;

    Set<Long> rolesIds = new HashSet<>();

    public UserDTO() {
    }

    public UserDTO(String name, String lastName, String email, Set<Long> rolesIds) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.rolesIds = rolesIds;
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

    public void setEmail(String email) {
        this.email = email;
    }

   public Set<Long> getRoles() {
        return rolesIds;
    }

   public void setRoles(Set<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }
}

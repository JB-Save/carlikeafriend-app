package com.carlikeafriend_backend.backend.dto;

import com.carlikeafriend_backend.backend.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class RegisterUserDTO {

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

    @NotBlank(message = "La Contraseña no debe estar vacía")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial.")
    private String password;

    Set<Role> roles = new HashSet<>();

    public RegisterUserDTO() {
    }

    public RegisterUserDTO(String name, String lastName, String email, String password, Set<Role> roles) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}

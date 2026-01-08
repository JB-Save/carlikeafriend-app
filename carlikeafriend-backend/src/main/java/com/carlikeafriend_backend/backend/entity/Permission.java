package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    @Version
    private Long version;

    public Permission() {
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    // Método de conveniencia para añadir un rol
    public void addRole(Role role) {
        this.roles.add(role);
        role.getPermissions().add(this);
    }

    // Método de conveniencia para eliminar un rol
    public void deleteRole(Role role) {
        this.roles.remove(role);
        role.getPermissions().remove(this);
    }
}

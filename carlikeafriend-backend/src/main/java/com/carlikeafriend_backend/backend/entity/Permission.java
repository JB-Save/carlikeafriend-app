package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Permission extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

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

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    private void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Permission that)) return false;

       if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    // Roles: (Colecciones con Set) - Método de conveniencia para añadir/eliminar un rol
    public void addRole(Role role) {
        if (role != null && this.roles.add(role)) {
            // Sincronizar el otro lado
            role.addPermission(this);
        }
    }

    public void removeRole(Role role) {
        if (role != null && this.roles.remove(role)) {
            // Sincronizar el otro lado
            role.removePermission(this);
        }
    }

    public boolean hasActiveRoles() {
        //Regla 1: No tiene roles activos.
        return  this.roles.stream().anyMatch(u -> !u.isDeleted());

    }
}

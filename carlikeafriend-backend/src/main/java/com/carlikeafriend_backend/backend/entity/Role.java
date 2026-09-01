package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "role", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Role extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    Set<Permission> permissions = new HashSet<>();

    @Version
    private Long version;

    public Role() {
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

    public Set<User> getUsers() {
        return Collections.unmodifiableSet(this.users);
    }

    private void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(this.permissions);
    }

    private void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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

        if (!(o instanceof Role that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    // Usuarios: Colecciones con Set. Método de conveniencia para añadir/remover un usuario (Inverse Side) -
    public void addUser(User user) {
        if (user != null && this.users.add(user)) {
            // Sincronizar el otro lado
            user.addRole(this);
        }
    }


    public void removeUser(User user) {
        if (user != null && this.users.remove(user)) {
            // Sincronizar el otro lado
            user.removeRole(this);
        }
    }

    // Permisos: Colecciones con Set (Owner Side)  - Aunque la lógica principal se gestiona en RoleService
    public void addPermission(Permission permission) {
        if (permission != null && this.permissions.add(permission)) {
            // Sincronizar el otro lado
            permission.addRole(this);

        }
    }

    public void removePermission(Permission permission) {
        if (permission != null && this.permissions.remove(permission)) {
            // Sincronizar el otro lado
            permission.removeRole(this);
        }
    }

    //Método para borrado Lógico de Role
    public boolean isBaseRole() {
        //Regla 1: Protección de roles del sistema
        return "ADMIN".equals(this.name) || "USER".equals(this.name);
    }

    public boolean hasActiveUsers() {
        //Regla 2: No tien usuarios activos. Devuelve TRUE si al menos un usuario NO está borrado
        return  this.users.stream().anyMatch(u -> !u.isDeleted());

    }
}


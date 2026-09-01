package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "maintenance_type", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
})
public class MaintenanceType extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(length = 65535) // Genera automáticamente un TEXT
    private String description;

    @Version
    private Long version;

    public MaintenanceType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        if (!(o instanceof MaintenanceType that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

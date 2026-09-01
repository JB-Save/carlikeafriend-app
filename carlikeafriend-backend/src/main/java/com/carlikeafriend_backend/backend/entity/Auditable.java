package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
// 1. Este listener escucha los eventos de persistencia (Save, Update)
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedBy // 2. Spring llena esto automáticamente usando AuditorAware
    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @CreatedDate // 3. Spring llena la fecha de creación automáticamente
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy // 4. Spring llena quién modificó automáticamente
    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @LastModifiedDate // 5. Spring llena la fecha de modificación automáticamente
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public Auditable() {
    }

    // Getters y Setters
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
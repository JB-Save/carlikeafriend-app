package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "icon")
public class Icon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;
    private String originalName;
    private String contentType;

    @OneToOne(mappedBy = "icon")
    @JsonIgnore
    // Esta anotación le indica a Jackson que ignore este campo durante la serialización a JSON sin anidamientos recursivos
    private Feature feature;

    public Icon() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {

        if (this.feature == feature) return; // 1. Cláusula de guarda

        Feature oldFeature = this.feature;
        this.feature = feature; // 2. Asignar nuevo valor

        // 3. Sincronización bidireccional
        if (oldFeature != null && oldFeature.getIcon() == this) {
            oldFeature.setIcon(null);
        }
        if (feature != null && feature.getIcon() != this) {
            feature.setIcon(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Icon that)) return false;

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

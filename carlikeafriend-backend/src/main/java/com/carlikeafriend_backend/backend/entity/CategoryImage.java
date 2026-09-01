package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "category_image")
public class CategoryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;
    private String originalName;
    private String contentType;

    @OneToOne(mappedBy = "categoryImage")
    @JsonIgnore
    // Esta anotación le indica a Jackson que ignore este campo durante la serialización a JSON sin anidamientos recursivos
    private Category category;

    public CategoryImage() {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (this.category == category) return; // 1. Cláusula de guarda

        Category oldCategory = this.category;
        this.category = category; // 2. Asignar nuevo valor

        // 3. Sincronización bidireccional
        if (oldCategory != null && oldCategory.getCategoryImage() == this) {
            oldCategory.setCategoryImage(null);
        }
        if (category != null && category.getCategoryImage() != this) {
            category.setCategoryImage(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CategoryImage that)) return false;

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

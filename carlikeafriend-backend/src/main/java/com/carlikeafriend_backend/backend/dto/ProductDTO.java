package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class ProductDTO {

    @NotBlank(message = "El Nombre no debe estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción no debe estar vacía")
    @Size(min = 10, max = 800, message = "La descripción debe tener entre 10 y 800 caracteres")
    private String description;

    @Min(value = 0, message = "El precio no debe ser negativo")
    private double price;

    private Set<Long> categoriesIds = new HashSet<>();

    private Set<Long> featuresIds = new HashSet<>();

    public ProductDTO() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Long> getCategories() {
        return categoriesIds;
    }

    public void setCategory(Set<Long> categoriesIds) {
        this.categoriesIds = categoriesIds;
    }

    public Set<Long> getFeatures() {
        return featuresIds;
    }

    public void setFeatures(Set<Long> featuresIds) {
        this.featuresIds = featuresIds;
    }
}

package com.carlikeafriend_backend.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private List<CategoryResponseForProductDTO> categories = new ArrayList<>();
    private List<FeatureResponseDTO> features = new ArrayList<>();
    private List<ImageDTO> productImages = new ArrayList<>();

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long id, String name, String description, double price, List<CategoryResponseForProductDTO> categories, List<FeatureResponseDTO> features,List<ImageDTO> productImages) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categories = categories;
        this.features = features;
        this.productImages = productImages;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<CategoryResponseForProductDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponseForProductDTO> categories) {
        this.categories = categories;
    }

    public List<FeatureResponseDTO> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureResponseDTO> features) {
        this.features = features;
    }

    public List<ImageDTO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ImageDTO>productImages) {
        this.productImages = productImages;
    }
}

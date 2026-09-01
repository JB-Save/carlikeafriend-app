package com.carlikeafriend_backend.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private SimpleResponseDTO make;
    private String description;
    private Integer passengerCapacity;
    private Integer baggageCapacity;
    private Integer numberOfDoors;
    private List<SimpleResponseDTO> categories = new ArrayList<>();
    private List<FeatureResponseDTO> features = new ArrayList<>();
    private Double price;
    private Double baseDepositAmount;
    private List<ImageDTO> productImages = new ArrayList<>();
    private List<PolicyCompleteResponseDTO> policies = new ArrayList<>();
    private Double averageRating;
    private Integer totalReviews;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long id,
                              String name,
                              SimpleResponseDTO make,
                              String description,
                              Integer passengerCapacity,
                              Integer baggageCapacity,
                              Integer numberOfDoors,
                              List<SimpleResponseDTO> categories,
                              List<FeatureResponseDTO> features,
                              Double price,
                              Double baseDepositAmount,
                              List<ImageDTO> productImages,
                              List<PolicyCompleteResponseDTO> policies,
                              Double averageRating,
                              Integer totalReviews) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.description = description;
        this.passengerCapacity = passengerCapacity;
        this.baggageCapacity = baggageCapacity;
        this.numberOfDoors = numberOfDoors;
        this.categories = categories;
        this.features = features;
        this.price = price;
        this.baseDepositAmount = baseDepositAmount;
        this.productImages = productImages;
        this.policies = policies;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
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

    public SimpleResponseDTO getMake() {
        return make;
    }

    public void setMake(SimpleResponseDTO make) {
        this.make = make;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public Integer getBaggageCapacity() {
        return baggageCapacity;
    }

    public void setBaggageCapacity(Integer baggageCapacity) {
        this.baggageCapacity = baggageCapacity;
    }

    public Integer getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(Integer numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public List<SimpleResponseDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<SimpleResponseDTO> categories) {
        this.categories = categories;
    }

    public List<FeatureResponseDTO> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureResponseDTO> features) {
        this.features = features;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getBaseDepositAmount() {
        return baseDepositAmount;
    }

    public void setBaseDepositAmount(Double baseDepositAmount) {
        this.baseDepositAmount = baseDepositAmount;
    }

    public List<ImageDTO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ImageDTO> productImages) {
        this.productImages = productImages;
    }

    public List<PolicyCompleteResponseDTO> getPolicies() {
        return policies;
    }

    public void setPolicies(List<PolicyCompleteResponseDTO> policies) {
        this.policies = policies;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
}

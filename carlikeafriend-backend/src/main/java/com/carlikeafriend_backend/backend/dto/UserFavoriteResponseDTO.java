package com.carlikeafriend_backend.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class UserFavoriteResponseDTO {
    private Long id;
    private String name;
    private SimpleResponseDTO make;
    private List<SimpleResponseDTO> categories = new ArrayList<>();
    private List<FeatureResponseDTO> features = new ArrayList<>();
    private List<ImageDTO> productImages = new ArrayList<>();
    private Double price;
    private Double averageRating;
    private Integer totalReviews;
    private Integer passengerCapacity;
    private Integer baggageCapacity;
    private Integer numberOfDoors;

    public UserFavoriteResponseDTO() {
    }

    public UserFavoriteResponseDTO(Long id,
                                   String name,
                                   SimpleResponseDTO make,
                                   List<SimpleResponseDTO> categories,
                                   List<FeatureResponseDTO> features,
                                   List<ImageDTO> productImages,
                                   Double price,
                                   Double averageRating,
                                   Integer totalReviews,
                                   Integer passengerCapacity,
                                   Integer baggageCapacity,
                                   Integer numberOfDoors) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.categories = categories;
        this.features = features;
        this.productImages = productImages;
        this.price = price;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.passengerCapacity = passengerCapacity;
        this.baggageCapacity = baggageCapacity;
        this.numberOfDoors = numberOfDoors;
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

    public List<ImageDTO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ImageDTO> productImages) {
        this.productImages = productImages;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
}

package com.carlikeafriend_backend.backend.dto;

public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double baseDailyRate;
    private Integer priority;
    private Double baseDepositAmount;
    private ImageDTO categoryImage;

    public CategoryResponseDTO() {
    }

    public CategoryResponseDTO(Long id, String name, String description, Double baseDailyRate, Integer priority, Double baseDepositAmount, ImageDTO categoryImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseDailyRate = baseDailyRate;
        this.priority = priority;
        this.baseDepositAmount = baseDepositAmount;
        this.categoryImage = categoryImage;
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

    public Double getBaseDailyRate() {
        return baseDailyRate;
    }

    public void setBaseDailyRate(Double baseDailyRate) {
        this.baseDailyRate = baseDailyRate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Double getBaseDepositAmount() {
        return baseDepositAmount;
    }

    public void setBaseDepositAmount(Double baseDepositAmount) {
        this.baseDepositAmount = baseDepositAmount;
    }

    public ImageDTO getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(ImageDTO categoryImage) {
        this.categoryImage = categoryImage;
    }
}

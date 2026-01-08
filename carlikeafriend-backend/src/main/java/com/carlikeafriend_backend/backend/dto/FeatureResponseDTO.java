package com.carlikeafriend_backend.backend.dto;

public class FeatureResponseDTO {

    private Long id;
    private String name;
    private ImageDTO icon;

    public FeatureResponseDTO() {
    }

    public FeatureResponseDTO(Long id, String name, ImageDTO icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
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

    public ImageDTO getIcon() {
        return icon;
    }

    public void setIcon(ImageDTO icon) {
        this.icon = icon;
    }
}

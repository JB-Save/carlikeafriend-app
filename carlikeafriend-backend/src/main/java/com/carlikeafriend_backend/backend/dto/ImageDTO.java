package com.carlikeafriend_backend.backend.dto;

public class ImageDTO {

    private Long id;
    private String imagePath;
    private String originalName;
    private String contentType;

    public ImageDTO() {
    }

    public ImageDTO(Long id, String imagePath, String originalName, String contentType) {
        this.id = id;
        this.imagePath = imagePath;
        this.originalName = originalName;
        this.contentType = contentType;
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
}

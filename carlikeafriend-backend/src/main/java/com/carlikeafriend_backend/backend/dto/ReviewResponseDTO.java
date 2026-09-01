package com.carlikeafriend_backend.backend.dto;

public class ReviewResponseDTO {

    private Long id;

    private SimpleResponseDTO user;
    private SimpleResponseDTO product;
    private Integer stars;
    private String comment;
    private String createdAt;

    public ReviewResponseDTO() {
    }

    public ReviewResponseDTO(Long id, SimpleResponseDTO user, SimpleResponseDTO product, Integer stars, String comment, String createdAt) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.stars = stars;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleResponseDTO getUser() {
        return user;
    }

    public void setUser(SimpleResponseDTO user) {
        this.user = user;
    }

    public SimpleResponseDTO getProduct() {
        return product;
    }

    public void setProduct(SimpleResponseDTO product) {
        this.product = product;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

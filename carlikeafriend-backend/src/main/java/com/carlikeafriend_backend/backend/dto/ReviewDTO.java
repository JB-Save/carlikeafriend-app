package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

public class ReviewDTO {
    @NotNull(message = "El vehículo es obligatorio")
    private Long productId; //productId hace referencia al catálogo (Product) relacionado con el vehículo (Vehicle)

    @Min(value = 1, message = "Mínimo 1 estrella")
    @Max(value = 5, message = "Máximo 5 estrellas")
    private Integer stars;

    @Size(max = 500, message = "El comentarío no debe exceder los 500 caracteres")
    private String comment;

    public ReviewDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}

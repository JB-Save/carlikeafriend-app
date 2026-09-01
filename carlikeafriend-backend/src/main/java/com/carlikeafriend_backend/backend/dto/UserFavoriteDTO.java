package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UserFavoriteDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    public UserFavoriteDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}

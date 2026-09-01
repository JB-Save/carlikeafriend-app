package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShareInteractionDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotBlank(message = "El nombre de la red social es obligatoria")
    @Size(max = 25, message = "El nombre de la red social no debe exceder los 25 caracteres")
    private String platform;

    @Size(max = 255, message = "El mensaje no debe exceder los 255 caracteres")
    private String customMessage;

    public ShareInteractionDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}

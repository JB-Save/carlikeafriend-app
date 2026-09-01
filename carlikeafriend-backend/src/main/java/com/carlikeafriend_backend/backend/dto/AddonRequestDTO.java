package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddonRequestDTO {
    @NotNull(message = "El extra es obligatorio")
    private Long addonId;

    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer quantity;

    public AddonRequestDTO() {
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

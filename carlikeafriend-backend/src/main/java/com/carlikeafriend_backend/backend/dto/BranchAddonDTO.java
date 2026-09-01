package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class BranchAddonDTO {
    @NotNull(message = "La sucursal es obligatoria")
    private Long branchId;

    @NotNull(message = "El extra es obligatorio")
    private Long addonId;

    @NotNull(message = "El stock total es obligatorio")
    @PositiveOrZero(message = "El stock total no puede ser negativo")
    private Integer totalStock;

    public BranchAddonDTO() {
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }
}

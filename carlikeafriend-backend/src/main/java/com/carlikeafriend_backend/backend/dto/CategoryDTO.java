package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

public class CategoryDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no debe ser negativo")
    private Double  baseDailyRate;

    @NotNull(message = "La prioridad es obligatoria")
    @PositiveOrZero(message = "La prioridad no debe ser negativa")
    private Integer priority;

    @NotNull(message = "El depósito base es obligatorio")
    @PositiveOrZero(message = "El depósito base no puede ser negativo")
    private Double baseDepositAmount;

    public CategoryDTO() {
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
}

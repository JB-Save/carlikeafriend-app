package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

public class ProductDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String name;

    @NotNull(message = "La marca es obligatoria")
    private Long makeId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 800, message = "La descripción no debe exceder los 800 caracteres")
    private String description;

    @NotNull(message = "La capacidad de pasajeros es obligatorio")
    @PositiveOrZero(message = "La capacidad de pasajeros no debe ser negativo")
    private Integer passengerCapacity;

    @NotNull(message = "La capacidad de equipaje es obligatorio")
    @PositiveOrZero(message = "La capacidad de equipaje no debe ser negativo")
    private Integer baggageCapacity;

    @NotNull(message = "El número de puertas es obligatorio")
    @PositiveOrZero(message = "El número de puertas no debe ser negativo")
    private Integer numberOfDoors;

    private Set<Long> categoriesIds = new HashSet<>();

    private Set<Long> featuresIds = new HashSet<>();

    private Set<Long> policiesIds = new HashSet<>();

    public ProductDTO() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   public Long getMakeId() {
        return makeId;
    }

    public void setMakeId(Long makeId) {
        this.makeId = makeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public Integer getBaggageCapacity() {
        return baggageCapacity;
    }

    public void setBaggageCapacity(Integer baggageCapacity) {
        this.baggageCapacity = baggageCapacity;
    }

    public Integer getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(Integer numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public Set<Long> getCategories() {
        return categoriesIds;
    }

    public void setCategory(Set<Long> categoriesIds) {
        this.categoriesIds = categoriesIds;
    }

    public Set<Long> getFeatures() {
        return featuresIds;
    }

    public void setFeatures(Set<Long> featuresIds) {
        this.featuresIds = featuresIds;
    }

    public Set<Long> getPolicies() {
        return policiesIds;
    }

    public void setPolicies(Set<Long> policiesIds) {
        this.policiesIds = policiesIds;
    }
}

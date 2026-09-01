package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MakeDTO {

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no debe exceder los 100 caracteres")
    private String name;

    public MakeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

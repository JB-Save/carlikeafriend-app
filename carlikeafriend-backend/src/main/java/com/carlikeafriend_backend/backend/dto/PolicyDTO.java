package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PolicyDTO {


    @NotBlank(message = "El nombre de la política es obligatoria")
    @Size(max = 30, message = "El nombre de la política no debe exceder los 30 caracteres")
    private String name;

    @NotNull(message = "El tipo de política es obligatorio")
    private Long policyTypeId;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 16777215, message = "El contenido no debe exceder los 16777215 caracteres")
    private String content;


    public PolicyDTO() {
    }

    public Long getPolicyTypeId() {
        return policyTypeId;
    }

    public void setPolicyTypeId(Long policyTypeId) {
        this.policyTypeId = policyTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}

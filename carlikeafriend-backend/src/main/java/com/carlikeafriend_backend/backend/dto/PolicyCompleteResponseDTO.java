package com.carlikeafriend_backend.backend.dto;


public class PolicyCompleteResponseDTO {

    private Long id;
    private String name;
    private String content;
    private SimpleResponseDTO policyType;

    public PolicyCompleteResponseDTO() {
    }

    public PolicyCompleteResponseDTO(Long id, String name, String content, SimpleResponseDTO policyType) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.policyType = policyType;
    }

    public PolicyCompleteResponseDTO(Long id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SimpleResponseDTO getPolicyType() {
        return policyType;
    }

    public void setPolicyType(SimpleResponseDTO policyType) {
        this.policyType = policyType;
    }
}

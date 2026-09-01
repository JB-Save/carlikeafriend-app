package com.carlikeafriend_backend.backend.dto;

import java.util.List;

public class CityBranchesResponseDTO {
    private Long id;
    private String name;
    private List<BranchShortResponseDTO> branches;

    public CityBranchesResponseDTO() {
    }

    public CityBranchesResponseDTO(Long id, String name, List<BranchShortResponseDTO> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches;
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

    public List<BranchShortResponseDTO> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchShortResponseDTO> branches) {
        this.branches = branches;
    }
}

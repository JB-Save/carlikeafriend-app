package com.carlikeafriend_backend.backend.dto;

public class BranchTransferFeeResponseDTO {

    private Long id;
    private SimpleResponseDTO originBranch;
    private SimpleResponseDTO destinationBranch;
    private Double feeAmount;

    public BranchTransferFeeResponseDTO() {
    }

    public BranchTransferFeeResponseDTO(Long id, SimpleResponseDTO originBranch, SimpleResponseDTO destinationBranch, Double feeAmount) {
        this.id = id;
        this.originBranch = originBranch;
        this.destinationBranch = destinationBranch;
        this.feeAmount = feeAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleResponseDTO getOriginBranch() {
        return originBranch;
    }

    public void setOriginBranch(SimpleResponseDTO originBranch) {
        this.originBranch = originBranch;
    }

    public SimpleResponseDTO getDestinationBranch() {
        return destinationBranch;
    }

    public void setDestinationBranch(SimpleResponseDTO destinationBranch) {
        this.destinationBranch = destinationBranch;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }
}

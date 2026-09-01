package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "branch_transfer_fee",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"origin_branch_id","destination_branch_id"})
})
public class BranchTransferFee extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_branch_id", nullable = false)
    @JsonIgnore
    private Branch originBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_branch_id", nullable = false)
    @JsonIgnore
    private Branch destinationBranch;

    private Double feeAmount;

    @Version
    private Long version;

    public BranchTransferFee() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Branch getOriginBranch() {
        return originBranch;
    }

    public void setOriginBranch(Branch originBranch) {
        this.originBranch = originBranch;
    }

    public Branch getDestinationBranch() {
        return destinationBranch;
    }

    public void setDestinationBranch(Branch destinationBranch) {
        this.destinationBranch = destinationBranch;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BranchTransferFee that)) return false;

        // Si el ID es nulo (entidad nueva sin guardar), solo son iguales si son
        // exactamente la misma instancia en memoria. Si ya tienen ID, los comparamos.
        return this.id != null && this.id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        // Retorna un hash constante para evitar que la entidad "desaparezca"
        // de colecciones Hash (como HashSet) antes y después de guardarse.
        return getClass().hashCode();
    }
}

package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "city", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class City extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "city", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Branch> branches = new ArrayList<>();

    @Version
    private Long version;

    public City() {
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

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(this.branches);
    }

    private void setBranches(List<Branch> branches) {
        this.branches = branches;
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

        if (!(o instanceof City that)) return false;

       if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    // Branch: Método de conveniencia
    public void addBranch(Branch branch) {
        if (branch != null && !this.branches.contains(branch)) {
            this.branches.add(branch);
            if (branch.getCity() != this) {
                branch.setCity(this);
            }
        }

    }

    public void removeBranch(Branch branch) {
        if (branch != null && this.branches.contains(branch)) {
            this.branches.remove(branch);
            if (branch.getCity() == this) {
                branch.setCity(null);
            }
        }
    }

    //Método para borrado Lógico de City
    public boolean hasActiveBranches() {
        // Regla 1: No tener sucursales activas
        return this.branches.stream().anyMatch(b -> !b.isDeleted());
    }

}

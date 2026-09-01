package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "policy_type", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class PolicyType extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "policyType", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();

    @Version
    private Long version;

    public PolicyType() {
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

    public List<Policy> getPolicies() {
        return Collections.unmodifiableList(this.policies);
    }

    private void setPolicies(List<Policy> policies) {
        this.policies = policies;
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

        if (!(o instanceof PolicyType that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }


    // Policy: Método de conveniencia
    public void addPolicy(Policy policy) {
        if (policy != null && !this.policies.contains(policy)) {
            this.policies.add(policy);
            if (policy.getPolicyType() != this) {
                policy.setPolicyType(this);
            }
        }

    }

    public void removePolicy(Policy policy) {
        if (policy != null && this.policies.contains(policy)) {
            this.policies.remove(policy);
            if (policy.getPolicyType() == this) {
                policy.setPolicyType(null);
            }
        }
    }

    //Método para borrado Lógico de PolicyType
    public boolean hasActivePolicies() {
        // Regla 1: No tener políticas activas
        return this.policies.stream().anyMatch(p -> !p.isDeleted());
    }

}


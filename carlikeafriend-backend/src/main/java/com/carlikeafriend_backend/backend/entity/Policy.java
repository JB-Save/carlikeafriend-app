package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "policy", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Policy extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_type_id")
    @JsonIgnore
    private PolicyType policyType;

    @Column(nullable = false)
    private String name;

    @Column(length = 16777215) // Genera automáticamente un MEDIUMTEXT
    private String content;

    @ManyToMany(mappedBy = "policies", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @Version
    private Long version;

    public Policy() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
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

    public List<Product> getProducts() {
        return Collections.unmodifiableList(this.products);
    }

    private void setProducts(List<Product> products) {
        this.products = products;
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

        if (!(o instanceof Policy that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    // Productos: Método de conveniencia para añadir/remover un producto (ManyToMany)
    public void addProduct(Product product) {
        if (product != null && !this.products.contains(product)) {
            this.products.add(product);
            // Sincronizar lado propietario
            if (!product.getPolicies().contains(this)) {
                product.addPolicy(this);
            }
        }
    }


    public void removeProduct(Product product) {
        if (product != null && this.products.contains(product)) {
            this.products.remove(product);
            // Sincronizar lado propietario
            product.removePolicy(this);
        }
    }

    //Método para borrado Lógico de Policy
    public boolean hasActiveProducts() {
        // Regla 1: No tien productos activos
        return this.products.stream().anyMatch(p -> !p.isDeleted());
    }

}

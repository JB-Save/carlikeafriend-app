package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "feature", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Feature extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "featureIcon_id", unique = true, nullable = false)
    private Icon icon;

    @ManyToMany(mappedBy = "features", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @Version
    private Long version;

    public Feature() {
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

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        if (this.icon == icon) return; // 1. Cláusula de guarda

        Icon oldIcon = this.icon;
        this.icon = icon; // 2. Asignar nuevo valor

        // 3. Sincronización bidireccional
        if (oldIcon != null && oldIcon.getFeature() == this) {
            oldIcon.setFeature(null);
        }
        if (icon != null && icon.getFeature() != this) {
            icon.setFeature(this);
        }

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

        if (!(o instanceof Feature that)) return false;

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
            if (!product.getFeatures().contains(this)) {
                product.addFeature(this);
            }
        }
    }


    public void removeProduct(Product product) {
        if (product != null && this.products.contains(product)) {
            this.products.remove(product);
            // Sincronizar lado propietario
            product.removeFeature(this);
        }
    }

    //Método para borrado Lógico de Feature
    public boolean hasActiveProducts() {
        // Regla 1: No tien productos activos
        return this.products.stream().anyMatch(p -> !p.isDeleted());
    }

}


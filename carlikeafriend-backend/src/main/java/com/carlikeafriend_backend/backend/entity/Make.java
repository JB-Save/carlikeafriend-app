package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "make", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Make extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "make", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    @Version
    private Long version;

    public Make() {
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

        if (!(o instanceof Make that)) return false;

       if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Productos: Método para añadir/remover con validación de duplicados
    public void addProduct(Product product) {
        if (product != null && !this.products.contains(product)) {
            this.products.add(product);
            if (product.getMake() != this) {
                product.setMake(this); // Sincroniza el lado "Muchos"
            }
        }
    }

    public void removeProduct(Product product) {
        if (product != null && this.products.contains(product)) {
            this.products.remove(product);
            if (product.getMake() == this) {
                product.setMake(null); // Quita la referencia en el "Muchos"
            }
        }
    }

    //Método para borrado Lógico de Make
    public boolean hasActiveProducts() {
        // Regla 1: No tener productos activos
        return this.products.stream().anyMatch(p -> !p.isDeleted());
    }

}

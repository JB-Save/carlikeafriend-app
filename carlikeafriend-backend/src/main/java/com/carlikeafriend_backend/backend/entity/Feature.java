package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feature", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "featureIcon_id", unique = true, nullable = false)
    private Icon icon;

    @ManyToMany(mappedBy = "features", fetch = FetchType.LAZY)
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
        this.icon = icon;
        if(icon != null){
            icon.setFeature(this);
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // Método de conveniencia para añadir un producto
    public void addProduct(Product product) {
        this.products.add(product);
        product.getFeatures().add(this);
    }

    // Método de conveniencia para eliminar un producto
    public void deleteProduct(Product product) {
        this.products.remove(product);
        product.getFeatures().remove(this);
    }

    // Método para eliminar el icono
    public void removeIcon(){
        if(this.icon != null){
            this.icon.setFeature(null);
            this.icon = null;
        }
    }

}


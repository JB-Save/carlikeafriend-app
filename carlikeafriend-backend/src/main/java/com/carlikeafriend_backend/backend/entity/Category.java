package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryImage_id", unique = true, nullable = false)
    private CategoryImage categoryImage;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    @Version
    private Long version;

    public Category() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryImage getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(CategoryImage image) {
        this.categoryImage = image;
        if(image != null){
            image.setCategory(this);
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
        product.getCategories().add(this);
    }

    // Método de conveniencia para eliminar un producto
    public void deleteProduct(Product product) {
        this.products.remove(product);
        product.getCategories().remove(this);
    }

    // Método para eliminar la imagen
    public void removeCategoryImage(){
        if(this.categoryImage != null){
            this.categoryImage.setCategory(null);
            this.categoryImage = null;
        }
    }

}


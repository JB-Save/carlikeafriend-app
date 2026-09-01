package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Category extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Double baseDailyRate;

    private Integer priority;

    // Depósito exigido por defecto para esta categoría
    private Double baseDepositAmount;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryImage_id", unique = true, nullable = false)
    private CategoryImage categoryImage;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore
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

    public Double getBaseDailyRate() {
        return baseDailyRate;
    }

    public void setBaseDailyRate(Double baseDailyRate) {
        this.baseDailyRate = baseDailyRate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Double getBaseDepositAmount() {
        return baseDepositAmount;
    }

    public void setBaseDepositAmount(Double baseDepositAmount) {
        this.baseDepositAmount = baseDepositAmount;
    }

    public CategoryImage getCategoryImage() {
        return categoryImage;
    }


    // Setter especial para Imagen (OneToOne)
    public void setCategoryImage(CategoryImage image) {

        if (this.categoryImage == image) return; // 1. Cláusula de guarda

        CategoryImage oldCategoryImage = this.categoryImage;
        this.categoryImage = image; // 2. Asignar nuevo valor

        // 3. Sincronización bidireccional
        if (oldCategoryImage != null && oldCategoryImage.getCategory() == this) {
            oldCategoryImage.setCategory(null);
        }
        if (image != null && image.getCategory() != this) {
            image.setCategory(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Category that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
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

    // Productos: Método de conveniencia para añadir/remover un producto (ManyToMany)
    public void addProduct(Product product) {
        if (product != null && !this.products.contains(product)) {
            this.products.add(product);
            // Sincronizar el lado propietario (Product)
            if (!product.getCategories().contains(this)) {
                product.addCategory(this);
            }
        }
    }

    public void removeProduct(Product product) {
        if (product != null && this.products.contains(product)) {
            this.products.remove(product);
            // Sincronizar el lado propietario
            product.removeCategory(this);
        }
    }

    //Método para borrado Lógico de Category
    public boolean hasActiveProducts() {
        // Regla 1: No tiene productos activos
      return this.products.stream().anyMatch(p -> !p.isDeleted());
    }

}


package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name") // Asegura que el nombre del producto sea único
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // El nombre no puede ser nulo
    private String name;

    private String description;
    private double price;

    // Un producto puede tener muchas imágenes
    // cascade = CascadeType.ALL: Operaciones en Producto se propagan a Image (ej. si borras un producto, borra sus imágenes)
    // orphanRemoval = true: Si una imagen se desvincula de un producto, se borra
    // fetch = FetchType.LAZY: Carga las imágenes solo cuando se accede a ellas
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @Version // Campo para el bloqueo optimista
    private Long version;

    public Product() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> imagePaths) {
        this.images = imagePaths;
    }

    // Métodos de conveniencia para añadir/remover imágenes
    public void addImage(Image image){
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(Image image){
        images.remove(image);
        image.setProduct(null);
    }
}

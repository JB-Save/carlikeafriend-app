package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "image")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;
    private String originalName;
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY) // Muchas imágenes pueden pertenecer a un producto
    @JoinColumn(name = "product_id", nullable = false) // Columna en la tabla Imagen que referencia a Producto
    @JsonIgnore // Esta anotación le indica a Jackson que ignore este campo durante la serialización a JSON sin anidamientos recursivos
    private Product product;

    public ProductImage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

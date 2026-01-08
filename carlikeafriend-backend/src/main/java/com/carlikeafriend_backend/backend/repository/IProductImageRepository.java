package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Método para encontrar el tipo de contenido por la ruta de la imagen
    @Query("SELECT ci.contentType FROM ProductImage ci WHERE ci.imagePath = :imagePath")
    Optional<String> findContentTypeByImagePath(@Param("imagePath") String imagePath);
}

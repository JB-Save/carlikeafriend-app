package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryImageRepository extends JpaRepository<CategoryImage, Long> {

    // Método para encontrar el tipo de contenido por la ruta de la imagen
    @Query("SELECT ci.contentType FROM CategoryImage ci WHERE ci.imagePath = :imagePath")
    Optional<String> findContentTypeByImagePath(@Param("imagePath") String imagePath);
}

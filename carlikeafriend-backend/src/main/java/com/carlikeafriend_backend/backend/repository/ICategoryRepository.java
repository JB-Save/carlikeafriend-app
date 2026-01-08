package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {

    // Método que verifica si existe una categoría con un nombre específico.
    boolean existsByName(String name);

    // Método que verifica si existe una categoría con un nombre, excluyendo un ID específico.
    boolean existsByNameAndIdNot(String name, Long id);
}

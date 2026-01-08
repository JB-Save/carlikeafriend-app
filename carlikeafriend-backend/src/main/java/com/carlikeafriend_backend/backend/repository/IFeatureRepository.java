package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFeatureRepository extends JpaRepository<Feature, Long> {

    // Método que verifica si existe una característica con un nombre específico.
    boolean existsByName(String name);

    // Método que verifica si existe una característica con un nombre, excluyendo un ID específico.
    boolean existsByNameAndIdNot(String name, Long id);
}

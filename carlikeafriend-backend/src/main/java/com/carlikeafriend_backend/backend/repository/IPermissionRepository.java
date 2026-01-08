package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    // Método que verifica si existe un permiso con un nombre específico.
    boolean existsByName(String name);

    // Método que verifica si existe un un permiso con un nombre, excluyendo un ID específico.
    boolean existsByNameAndIdNot(String name, Long id);
}

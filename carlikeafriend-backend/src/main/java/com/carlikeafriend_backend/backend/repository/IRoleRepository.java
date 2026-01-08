package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    // Método que verifica si existe un rol con un nombre específico.
    boolean existsByName(String name);

    //Método que verifica si existe un rol con un nombre específico.
   Optional<Role> findByName(String name);

    // Método que verifica si existe un un rol con un nombre, excluyendo un ID específico.
    boolean existsByNameAndIdNot(String name, Long id);
}

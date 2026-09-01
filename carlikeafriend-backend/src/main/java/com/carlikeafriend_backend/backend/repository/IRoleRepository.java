package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

     /** Métodos de consulta que incluyen el estado: deleted (Borrado Lógico) **/
    // Buscar solo activas
    Optional<Role> findByIdAndDeletedFalse(Long id);

    //Método que verifica si existe un rol con un nombre específico.
    Optional<Role> findByNameAndDeletedFalse(String name);

    // Listar solo activas
    List<Role> findAllByDeletedFalse();

    // Validar nombre único entre las activas (para creación)
    boolean existsByNameAndDeletedFalse(String name);

    // Validar nombre único excluyendo la actual y borradas (para actualización)
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);
}

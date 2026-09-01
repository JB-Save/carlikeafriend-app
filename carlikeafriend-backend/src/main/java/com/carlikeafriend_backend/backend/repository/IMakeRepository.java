package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Make;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMakeRepository extends JpaRepository<Make, Long> {

    /** Métodos de consulta que incluyen el estado: deleted (Borrado Lógico) **/
    // Buscar solo activas
    Optional<Make> findByIdAndDeletedFalse(Long id);

    // Listar solo activas
    List<Make> findAllByDeletedFalse();

    // Validar nombre único entre las activas (para creación)
    boolean existsByNameAndDeletedFalse(String name);

    // Validar nombre único excluyendo la actual y borradas (para actualización)
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);

}

package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.MaintenanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMaintenanceTypeRepository extends JpaRepository<MaintenanceType, Long> {

    /** Métodos de consulta que incluyen el estado: deleted (Borrado Lógico) **/
    // Buscar solo activas
    Optional<MaintenanceType> findByIdAndDeletedFalse(Long id);

    // Listar solo activas
    List<MaintenanceType> findAllByDeletedFalse();

    // Validar código único entre las activas (para creación)
    boolean existsByCodeAndDeletedFalse(String code);

    // Validar nombre único excluyendo la actual y borradas (para actualización)
    boolean existsByCodeAndIdNotAndDeletedFalse(String code, Long id);
}

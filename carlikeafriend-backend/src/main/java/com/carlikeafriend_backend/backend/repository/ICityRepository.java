package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICityRepository extends JpaRepository<City, Long> {

    /** Métodos de consulta que incluyen el estado: deleted (Borrado Lógico) **/
    // Buscar solo activas
    Optional<City> findByIdAndDeletedFalse(Long id);

    // Listar solo activas
    List<City> findAllByDeletedFalse();

    //Trae las ciudades y sus sedes
    @Query("SELECT DISTINCT c FROM City c LEFT JOIN FETCH c.branches b " +
            "WHERE c.deleted = false AND (b IS NULL OR b.deleted = false)")
    List<City> findAllActiveWithBranches();

    // Validar nombre único entre las activas (para creación)
    boolean existsByNameAndDeletedFalse(String name);

    // Validar nombre único excluyendo la actual y borradas (para actualización)
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);
}

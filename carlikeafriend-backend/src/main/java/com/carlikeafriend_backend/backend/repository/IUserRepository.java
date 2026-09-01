package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    /** Métodos de consulta que incluyen el estado: deleted (Borrado Lógico) **/
    // Buscar solo activas
    Optional<User> findByIdAndDeletedFalse(Long id);

    // Listar solo activas
    List<User> findAllByDeletedFalse();

    // Validar email único entre usuarios activos (para creación)
    boolean existsByEmailAndDeletedFalse(String email);

    // Validar email único excluyendo la actual y borradas (para actualización)
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmailWithRolesAndPermissionsAndDeleteFalse(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " + // Usamos JOIN para filtrar
            "LEFT JOIN FETCH u.roles " + // Usamos FETCH para cargar todos los roles del usuario
            "WHERE r.name = 'ADMIN' AND u.deleted = false AND u.isEnabled = true")
    List<User> findAllAdmins();
}

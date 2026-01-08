package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    // Método que verifica si existe un usuario con un email específico.
    boolean existsByEmail(String email); // Método para buscar por email

    /*
     Busca un usuario por email, pero usa JOIN FETCH para cargar
     las colecciones LAZY (roles y permisos) dentro de la misma transacción.
     Esto evita la LazyInitializationException en el JwtAuthenticationFilter y en el UserService.login.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

    // Método que verifica si existe un usuario con un email, excluyendo un ID específico.
    boolean existsByEmailAndIdNot(String email, Long id);
}

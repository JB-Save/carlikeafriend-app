package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    // Check rápido para el catálogo
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Borrado atómico para el "desmarcar"
    void deleteByUserIdAndProductId(Long userId, Long productId);

    // Para obtener la lista de la sección "Mis Favoritos" de forma eficiente
    @Query("SELECT f.product FROM UserFavorite f WHERE f.user.id = :userId")
    List<Product> findAllFavoriteProductsByUserId(@Param("userId") Long userId);
}

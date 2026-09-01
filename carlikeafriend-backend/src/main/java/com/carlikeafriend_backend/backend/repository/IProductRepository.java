package com.carlikeafriend_backend.backend.repository;


import com.carlikeafriend_backend.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

     /** Métodos de consulta que incluyen el estado: deleted (Borrado Lógico) **/
    // Buscar solo activas
    Optional<Product> findByIdAndDeletedFalse(Long id);

    // Listar solo activas
    List<Product> findAllByDeletedFalse();

    @Query("SELECT p FROM Product p " +
            "WHERE p.deleted = false " +
            "AND p.averageRating >= 4.2 " +
            "AND p.totalReviews >= 20 " +
            "AND EXISTS (" +
            "   SELECT 1 FROM Vehicle v " +
            "   WHERE v.product.id = p.id " +
            "   AND v.deleted = false " +
            "   AND v.vehicleStatus = 'AVAILABLE' " +
            "   AND v.currentBranch.deleted = false" +
            ") " +
            "ORDER BY p.price ASC")
    List<Product> findTopRecommendedProducts();

    // Validar nombre único entre las activas (para creación)
    boolean existsByNameAndDeletedFalse(String name);

    // Validar nombre único excluyendo la actual y borradas (para actualización)
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);

    // Método para buscar todos los productos activos que tienen esta categoría
    List<Product> findAllByCategoriesIdAndDeletedFalse(Long categoryId);


}

package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {

    // Proyección para obtener las estadísticas calculadas por la BD
    @Query("SELECT COALESCE(AVG(r.stars), 0.0), COUNT(r.id) FROM Review r WHERE r.vehicle.product.id = :productId")
    List<Object[]> getRatingStatsByProductId(@Param("productId") Long productId);

    //verificar en el servicio si la reserva ya fue calificada
    boolean existsByReservationId(UUID reservationId);

    //Set Ids de reservas que tienen reseña
    @Query("SELECT r.reservation.id FROM Review r WHERE r.reservation.id IN :reservationIds")
    Set<UUID> findReviewedReservationIds(@Param("reservationIds") List<UUID> reservationIds);

    // Listar las reseñas
    @Query("SELECT r FROM Review r WHERE r.vehicle.product.id = :productId ORDER BY r.createdAt DESC")
    List<Review> findByProductId(@Param("productId") Long productId);

}

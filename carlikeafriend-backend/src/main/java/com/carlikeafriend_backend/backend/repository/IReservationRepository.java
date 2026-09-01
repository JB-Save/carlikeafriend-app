package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Reservation;
import com.carlikeafriend_backend.backend.entity.ReservationStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.vehicle.product.id = :productId AND r.reservationStatus = :status ORDER BY r.returnDatetime DESC LIMIT 1")
    Optional<Reservation> findLatestCompletedReservationByUserAndProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("status") ReservationStatus status
    );

    // Buscamos reservas en un estado específico creadas antes de una fecha límite
    List<Reservation> findByReservationStatusAndCreatedAtBefore(ReservationStatus reservationStatus, LocalDateTime dateTime);

    @Query("SELECT r FROM Reservation r WHERE r.vehicle.product.id = :productId " +
            "AND r.pickupBranch.id = :branchId " + // <-- FILTRO DE SUCURSAL
            "AND r.reservationStatus NOT IN ('CANCELLED', 'COMPLETED') " +
            "AND (r.pickupDatetime <= :endDate AND r.returnDatetime >= :startDate)")
    List<Reservation> findActiveReservationsForProductAndBranchInDateRange(
            @Param("productId") Long productId,
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Verifica si un vehículo ya está reservado en el rango de fechas solicitado
    @Query("""
                SELECT COUNT(r) > 0 FROM Reservation r 
                WHERE r.vehicle.id = :vehicleId 
                AND r.reservationStatus NOT IN ('CANCELLED', 'COMPLETED') 
                AND (r.pickupDatetime < :returnDate AND r.returnDatetime > :pickupDate)
            """)
    boolean isVehicleBooked(
            @Param("vehicleId") Long vehicleId,
            @Param("pickupDate") LocalDateTime pickupDate,
            @Param("returnDate") LocalDateTime returnDate
    );

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.pickupBranch " +
            "LEFT JOIN FETCH r.returnBranch " +
            "WHERE r.user.id = :userId " +
            "AND r.reservationStatus IN :statuses")
    List<Reservation> findByUserIdAndReservationStatusIn(
            @Param("userId") Long userId,
            @Param("statuses") Collection<ReservationStatus> statuses,
            Sort sort // Mantenemos Sort para optimizar el ordenamiento en BD
    );
}

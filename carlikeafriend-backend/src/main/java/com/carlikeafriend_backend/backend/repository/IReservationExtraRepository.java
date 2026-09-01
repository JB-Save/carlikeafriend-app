package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.ReservationExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IReservationExtraRepository extends JpaRepository<ReservationExtra, Long> {

    // Esta query es el corazón del sistema. Calcula cuántos extras están en uso.
    // Dos rangos de fechas se solapan si: Inicio1 < Fin2 Y Fin1 > Inicio2
    @Query("SELECT COALESCE(SUM(re.quantity), 0) FROM ReservationExtra re " +
            "JOIN re.reservation r " +
            "WHERE r.pickupBranch.id = :branchId " +
            "AND re.addon.id = :addonId " +
            "AND r.reservationStatus IN ('PENDING_CONFIRMATION', 'CONFIRMED', 'IN_PROGRESS') " +
            "AND r.pickupDatetime < :returnDate " +
            "AND r.returnDatetime > :pickupDate")
    Integer sumQuantityReservedInDateRange(
            @Param("branchId") Long branchId,
            @Param("addonId") Long addonId,
            @Param("pickupDate") LocalDateTime pickupDate,
            @Param("returnDate") LocalDateTime returnDate
    );
}

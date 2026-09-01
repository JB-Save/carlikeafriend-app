package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IInspectionRepository extends JpaRepository<Inspection, Long> {
    // Útil para listar el historial de inspecciones de una reserva específica
    List<Inspection> findByReservationId(UUID reservationId);
}

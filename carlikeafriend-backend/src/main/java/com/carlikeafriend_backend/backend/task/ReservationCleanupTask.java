package com.carlikeafriend_backend.backend.task;

import com.carlikeafriend_backend.backend.entity.Reservation;
import com.carlikeafriend_backend.backend.entity.ReservationStatus;
import com.carlikeafriend_backend.backend.repository.IReservationRepository;
import com.carlikeafriend_backend.backend.service.IReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class ReservationCleanupTask {

    private final IReservationRepository reservationRepository;
    private final IReservationService reservationService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationCleanupTask.class);

    @Autowired
    public ReservationCleanupTask(IReservationRepository reservationRepository, IReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    // Se ejecuta cada 2 minutos
    @Scheduled(fixedRate = 120000)
    @Transactional
    public void cleanupExpiredReservations() {
        // Definimos el TTL (Tiempo de vida) de la pre-reserva: 10 minutos
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Reservation> expired = reservationRepository
                .findByReservationStatusAndCreatedAtBefore(ReservationStatus.PENDING_CONFIRMATION, threshold);

        if (!expired.isEmpty()) {
            logger.info("Encontradas {} reservas expiradas. Procediendo a liberar.", expired.size());
            expired.forEach(res -> {
                try {
                    // Llamamos al método diseñado para procesos batch/sistema
                    reservationService.cancelReservationBySystem(res.getId(), "Expiración de tiempo en pasarela (10 min).");
                } catch (Exception e) {
                    logger.error("Fallo al cancelar reserva {}: {}", res.getId(), e.getMessage());
                }
            });
        }
    }
}

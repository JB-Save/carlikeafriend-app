package com.carlikeafriend_backend.backend.util;

import com.carlikeafriend_backend.backend.dto.FinancialConfigurationResponseDTO;
import com.carlikeafriend_backend.backend.exception.BookingStateConflictException;
import com.carlikeafriend_backend.backend.exception.InvalidRangeException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utilidad centralizada para validar reglas de negocio de fechas y horas.
 * Mantiene la paridad entre el comportamiento del frontend y el backend.
 */
public class DateValidationUtils {

    // Margen de tolerancia (en minutos) para compensar latencia de red,
    // desajuste de relojes y el tiempo que toma procesar el request.
    private static final int TOLERANCE_MINUTES = 5;

    /**
     * Calcula la cantidad de días de alquiler basado en la lógica de 24 horas.
     * Cualquier fracción de tiempo adicional se cuenta como un día extra (Math.ceil).
     * Mínimo devuelve 1 día.
     */
    public static long calculateRentalDays(LocalDateTime pickup, LocalDateTime returnDate) {
        if (pickup == null || returnDate == null) return 0L;

        long minutes = Duration.between(pickup, returnDate).toMinutes();
        // 1440 minutos = 24 horas
        return Math.max(1L, (long) Math.ceil(minutes / 1440.0));
    }

    /**
     * Valida si un conductor cumple con la edad mínima requerida para la fecha de recogida.
     * * @param birthDate Fecha de nacimiento del conductor
     *
     * @param pickupDateTime Fecha y hora de recogida programada
     * @param minAge         Edad mínima permitida (generalmente 18)
     */
    public static void validateDriverAge(LocalDate birthDate, LocalDateTime pickupDateTime, int minAge) {
        if (birthDate == null || pickupDateTime == null) return;

        LocalDate pickupDate = pickupDateTime.toLocalDate();
        long ageAtPickup = ChronoUnit.YEARS.between(birthDate, pickupDate);

        if (ageAtPickup < minAge) {
            throw new BookingStateConflictException("Para la fecha de recogida " + pickupDate +
                    ", el conductor tendrá " + ageAtPickup + " años. Se requiere ser mayor de " + minAge + " años.");
        }
    }

    /**
     * Valida que la licencia de conducir no esté vencida en el periodo del alquiler.
     *
     * @param licenseExpirationDate Fecha de vencimiento de la licencia
     * @param returnDateTime        Fecha y hora de entrega programada
     */
    public static void validateLicenseExpiration(LocalDate licenseExpirationDate, LocalDateTime returnDateTime) {
        if (licenseExpirationDate == null || returnDateTime == null) return;

        LocalDate returnDate = returnDateTime.toLocalDate();

        if (licenseExpirationDate.isBefore(returnDate)) {
            throw new BookingStateConflictException("No puede reservar: Su licencia de conducir vence el " + licenseExpirationDate +
                    ", antes o durante el periodo de alquiler.");
        }
    }

    /**
     * Determina si una cancelación está dentro del periodo de penalización (ej. 24 horas antes).
     */
    public static boolean isWithinPenaltyWindow(LocalDateTime now, LocalDateTime pickupDate, long penaltyHours) {
        if (now == null || pickupDate == null) return false;
        long hoursUntilPickup = ChronoUnit.HOURS.between(now, pickupDate);
        return hoursUntilPickup < penaltyHours;
    }


    private static LocalDateTime getNextAvailableBlock(LocalDateTime time) {
        int minute = time.getMinute();

        if (minute < 30) {
            // Si los minutos son < 30 (ej. 07:00 a 07:29), el siguiente bloque es a la media hora (07:30)
            return time.withMinute(30).withSecond(0).withNano(0);
        } else {
            // Si los minutos son >= 30 (ej. 07:30 a 07:59), el siguiente bloque es la siguiente hora en punto (08:00)
            return time.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
    }

    public static void validateBookingDates(LocalDateTime pickupDate, LocalDateTime returnDate, FinancialConfigurationResponseDTO config, String context) {
        if (pickupDate == null || returnDate == null) return;

        LocalDateTime realNow = LocalDateTime.now();
        // Calculamos un "ahora" tolerante restando los minutos de gracia.
        // Toda validación hacia el "pasado inmediato" se hace con esta fecha.
        LocalDateTime tolerantNow = realNow.minusMinutes(TOLERANCE_MINUTES);

        String ctx = context != null ? context + ": " : "";

        // 1. Validar que la recogida sea en el futuro (usando el tolerantNow)
        if (!pickupDate.isAfter(tolerantNow)) {
            throw new InvalidRangeException(ctx + "La fecha y hora de recogida ha expirado. Por favor, actualice su búsqueda.");
        }

        // 2. Validar que la entrega no sea en el pasado
        if (returnDate.isBefore(tolerantNow)) {
            throw new InvalidRangeException(ctx + "La fecha y hora de entrega no puede estar en el pasado.");
        }

        // 3. Si la recogida es hoy -> debe ser al menos el siguiente bloque hábil de "tolerantNow"
        // Evaluamos tanto contra realNow como tolerantNow por si la resta cruzó la medianoche
        if (pickupDate.toLocalDate().equals(realNow.toLocalDate()) || pickupDate.toLocalDate().equals(tolerantNow.toLocalDate())) {
            LocalDateTime minPickupTime = getNextAvailableBlock(tolerantNow);
            if (pickupDate.isBefore(minPickupTime)) {
                throw new InvalidRangeException(ctx + "La hora de recogida seleccionada ya no está disponible. Siguiente horario hábil: " + minPickupTime.toLocalTime());
            }
        }

        // 4. Si la entrega es hoy -> debe ser al menos la siguiente hora hábil después de la recogida (min 1 hora de diferencia)
        if (returnDate.toLocalDate().equals(pickupDate.toLocalDate())) {
            LocalDateTime minReturnTime = pickupDate.plusHours(1);
            if (returnDate.isBefore(minReturnTime)) {
                throw new InvalidRangeException(ctx + "Si la entrega es el mismo día que la recogida, debe haber al menos 1 hora de diferencia (" + minReturnTime.toLocalTime() + " en adelante).");
            }
        }

        // 5. Validación general de orden cronológico
        if (returnDate.isBefore(pickupDate) || returnDate.isEqual(pickupDate)) {
            throw new InvalidRangeException(ctx + "La entrega debe ser estrictamente posterior a la recogida.");
        }

        // 6. Validar rangos de 30 minutos (00 o 30 minutos)
        if (pickupDate.getMinute() % 30 != 0 || returnDate.getMinute() % 30 != 0) {
            throw new InvalidRangeException(ctx + "Las horas deben estar en intervalos de 30 minutos exactos (ej. 10:00, 10:30).");
        }

        // 7. Validar el máximo de 30 días permitidos para el alquiler (Seguridad de API)
        long rentalDays = calculateRentalDays(pickupDate, returnDate);
        if (rentalDays > config.getMaxRentalDays()) {
            throw new InvalidRangeException(ctx + "El periodo máximo de alquiler permitido es de " + config.getMaxRentalDays() + " días.");
        }

    }
}

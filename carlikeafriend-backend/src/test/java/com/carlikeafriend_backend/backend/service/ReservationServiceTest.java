package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ResponseBlockedDatesDTO;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.BookingStateConflictException;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.impl.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock private IReservationRepository reservationRepository;
    @Mock private IUserRepository userRepository;
    @Mock private IVehicleRepository vehicleRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("startRental - Inicia el alquiler correctamente cuando cumple las precondiciones")
    void startRental_Success() {
        UUID resId = UUID.randomUUID();
        Long employeeId = 2L;

        Reservation reservation = spy(new Reservation());
        reservation.setId(resId);
        reservation.setPickupDatetime(LocalDateTime.now().plusHours(1)); // Dentro de las 2 horas permitidas
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);

        // Simular inspección de entrega (Pickup)
        Inspection pickupInspection = new Inspection();
        pickupInspection.setInspectionType(InspectionType.PICKUP);
        reservation.addInspection(pickupInspection);

        Vehicle vehicle = spy(new Vehicle());
        vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
        reservation.setVehicle(vehicle);

        User employee = new User();
        employee.setId(employeeId);

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByIdAndDeletedFalse(employeeId)).thenReturn(Optional.of(employee));

        reservationService.startRental(resId, employeeId);

        verify(reservation).startRental();
        verify(vehicle).rentOut();
        verify(reservationRepository).save(reservation);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    @DisplayName("startRental - Lanza excepción si la reserva no tiene inspección de entrega")
    void startRental_WithoutInspection_ThrowsException() {
        UUID resId = UUID.randomUUID();
        Long employeeId = 2L;

        Reservation reservation = new Reservation();
        reservation.setId(resId);
        reservation.setPickupDatetime(LocalDateTime.now().plusHours(1));
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        // Sin inspecciones agregadas

        User employee = new User();
        employee.setId(employeeId);

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByIdAndDeletedFalse(employeeId)).thenReturn(Optional.of(employee));

        assertThrows(BookingStateConflictException.class, () -> reservationService.startRental(resId, employeeId));
    }

    @Test
    @DisplayName("completeRental - Completa el alquiler, libera el vehículo y actualiza kilometraje")
    void completeRental_Success() {
        UUID resId = UUID.randomUUID();
        Long employeeId = 2L;

        Reservation reservation = spy(new Reservation());
        reservation.setId(resId);
        reservation.setReservationStatus(ReservationStatus.IN_PROGRESS);

        Inspection returnInspection = new Inspection();
        returnInspection.setInspectionType(InspectionType.RETURN);
        returnInspection.setMileage(15000);
        reservation.addInspection(returnInspection);

        Branch returnBranch = new Branch();
        returnBranch.setId(5L);
        reservation.setReturnBranch(returnBranch);

        Vehicle vehicle = spy(new Vehicle());
        vehicle.setCurrentMileage(10000);
        reservation.setVehicle(vehicle);

        User employee = new User();
        employee.setId(employeeId);

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByIdAndDeletedFalse(employeeId)).thenReturn(Optional.of(employee));

        reservationService.completeRental(resId, employeeId);

        verify(reservation).completeRental();
        verify(vehicle).setCurrentMileage(15000);
        verify(vehicle).setCurrentBranch(returnBranch);
        verify(vehicle).release();
        verify(reservationRepository).save(reservation);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    @DisplayName("getBlockedDatesForProduct - Retorna todas las fechas bloqueadas si no hay inventario físico")
    void getBlockedDatesForProduct_ZeroStock_ReturnsAllDates() {
        Long productId = 10L;
        Long branchId = 1L;
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        when(vehicleRepository.countAvailableVehiclesByProductIdAndBranchId(productId, branchId)).thenReturn(0);

        ResponseBlockedDatesDTO result = reservationService.getBlockedDatesForProduct(productId, branchId, start, end);

        assertEquals(3, result.getBlockedDates().size());
    }
}
package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.InspectionDTO;
import com.carlikeafriend_backend.backend.dto.InspectionResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IInspectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carlikeafriend")
public class InspectionController {

    private final IInspectionService inspectionService;

    @Autowired
    public InspectionController(IInspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    /**
     * Crea una nueva inspección (Pickup o Return) para una reserva.
     * Solo los administradores y empleados pueden realizar esta acción.
     */
    @PostMapping
    public ResponseEntity<InspectionResponseDTO> createInspection(
            @RequestBody @Valid InspectionDTO request,
            @AuthenticationPrincipal User currentEmployee) {

        // Pasamos el ID del empleado que está logueado en el sistema haciendo la inspección
        InspectionResponseDTO response = inspectionService.createInspection(currentEmployee.getId(), request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene el historial de inspecciones de una reserva específica.
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<InspectionResponseDTO>> getInspectionsByReservation(
            @PathVariable UUID reservationId) {
       return new ResponseEntity<>(inspectionService.getInspectionsByReservation(reservationId), HttpStatus.OK);
    }
}

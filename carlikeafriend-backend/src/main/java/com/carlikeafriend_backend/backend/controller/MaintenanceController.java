package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.MaintenanceDTO;
import com.carlikeafriend_backend.backend.dto.MaintenanceResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IMaintenanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carlikeafriend")
public class MaintenanceController {
    private final IMaintenanceService maintenanceService;

    @Autowired
    public MaintenanceController(IMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PutMapping("/vehicles/{vehicleId}/start-maintenance")
    public ResponseEntity<String> sendToMaintenance(@PathVariable Long vehicleId) {
        maintenanceService.sendVehicleToMaintenance(vehicleId);
        return new ResponseEntity<>("Vehículo enviado a mantenimiento. Estado actualizado a MAINTENANCE.", HttpStatus.OK);
    }

    @PostMapping("/vehicles/{vehicleId}/maintenances")
    public ResponseEntity<MaintenanceResponseDTO> registerMaintenanceCompleted(
            @PathVariable Long vehicleId,
            @Valid @RequestBody MaintenanceDTO request,
            @AuthenticationPrincipal User currentUser) {

        MaintenanceResponseDTO response = maintenanceService.registerMaintenanceCompleted(
                vehicleId, currentUser.getId(), request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

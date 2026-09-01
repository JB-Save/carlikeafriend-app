package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.VehicleDTO;
import com.carlikeafriend_backend.backend.dto.VehicleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IVehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class VehicleController {

    private final IVehicleService vehicleService;

    @Autowired
    public VehicleController(IVehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponseDTO> saveVehicle(@RequestBody @Valid VehicleDTO vehicleDTO)
            throws DuplicateResourceException {
        VehicleResponseDTO savedVehicle = vehicleService.saveVehicle(vehicleDTO);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        return new ResponseEntity<>(vehicleService.getAllVehicles(), HttpStatus.OK);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        Optional<VehicleResponseDTO> vehicleDTO = vehicleService.getVehicleById(id);
        return vehicleDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/vehicles/{id}")
    //@PatchMapping("/vehicles/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable Long id,
            @RequestBody @Valid VehicleDTO vehicleDTO)
            throws DuplicateResourceException {

        VehicleResponseDTO updatedVehicle = vehicleService.updateVehicle(id, vehicleDTO);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);

    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/vehicles/restore/{plate}")
    public ResponseEntity<VehicleResponseDTO> restoreVehicle(@PathVariable String plate) throws DuplicateResourceException {
        VehicleResponseDTO restoredVehicle = vehicleService.restoreVehicle(plate);
        return new ResponseEntity<>(restoredVehicle, HttpStatus.OK);
    }
}

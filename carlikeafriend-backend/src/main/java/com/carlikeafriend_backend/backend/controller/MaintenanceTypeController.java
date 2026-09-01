package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.MaintenanceTypeCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.MaintenanceTypeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IMaintenanceTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class MaintenanceTypeController {

    private final IMaintenanceTypeService maintenanceTypeService;

    @Autowired
    public MaintenanceTypeController(IMaintenanceTypeService maintenanceTypeService) {
        this.maintenanceTypeService = maintenanceTypeService;
    }

    @PostMapping("/maintenances/types")
    public ResponseEntity<SimpleResponseDTO> saveMaintenanceType(@RequestBody @Valid MaintenanceTypeDTO maintenanceTypeDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedMaintenanceType = maintenanceTypeService.saveMaintenanceType(maintenanceTypeDTO);
        return new ResponseEntity<>(savedMaintenanceType, HttpStatus.CREATED);
    }

    @GetMapping("/maintenances/types")
    public ResponseEntity<List<SimpleResponseDTO>> getAllMaintenanceTypes() {
        return new ResponseEntity<>(maintenanceTypeService.getAllMaintenanceTypes(), HttpStatus.OK);
    }

    @GetMapping("/maintenances/{id}/types")
    public ResponseEntity<MaintenanceTypeCompleteResponseDTO> getMaintenanceTypeById(@PathVariable Long id) {
        Optional<MaintenanceTypeCompleteResponseDTO> maintenanceTypeResponseDTO = maintenanceTypeService.getMaintenanceTypeById(id);
        return maintenanceTypeResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/maintenances/{id}/types")
    //@PatchMapping("/maintenances/types/{id}")
    public ResponseEntity<SimpleResponseDTO> updateMaintenanceType(
            @PathVariable Long id,
            @RequestBody @Valid MaintenanceTypeDTO maintenanceTypeDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO updatedMaintenanceType = maintenanceTypeService.updateMaintenanceType(id, maintenanceTypeDTO);
        return new ResponseEntity<>(updatedMaintenanceType, HttpStatus.OK);
    }

    @DeleteMapping("/maintenances/{id}/types")
    public ResponseEntity<Void> deleteMaintenanceType(@PathVariable Long id) {
        maintenanceTypeService.deleteMaintenanceType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

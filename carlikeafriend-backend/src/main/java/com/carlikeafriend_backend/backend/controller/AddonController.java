package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.AddonDTO;
import com.carlikeafriend_backend.backend.dto.AddonResponseDTO;
import com.carlikeafriend_backend.backend.service.IAddonService;
import com.carlikeafriend_backend.backend.service.IBranchAddonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class AddonController {

    private final IAddonService addonService;
    private final IBranchAddonService branchAddonService;

    @Autowired
    public AddonController(IAddonService addonService, IBranchAddonService branchAddonService) {
        this.addonService = addonService;
        this.branchAddonService = branchAddonService;
    }

    // --- CRUD DEL CATÁLOGO DE EXTRAS ---

    @PostMapping("/addons")
    public ResponseEntity<AddonResponseDTO> createAddon(@RequestBody @Valid AddonDTO addonDTO) {
        return new ResponseEntity<>(addonService.createAddon(addonDTO), HttpStatus.CREATED);
    }

    @PutMapping("/addons/{id}")
    public ResponseEntity<AddonResponseDTO> updateAddon(@PathVariable Long id, @RequestBody @Valid AddonDTO addonDTO) {
        return new ResponseEntity<>(addonService.updateAddon(id, addonDTO), HttpStatus.OK);
    }

    @DeleteMapping("/addons/{id}")
    public ResponseEntity<Void> deleteAddon(@PathVariable Long id) {
        addonService.deleteAddon(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/addons")
    public ResponseEntity<List<AddonResponseDTO>> getAllAddons() {
        return new ResponseEntity<>(addonService.getAllAddons(), HttpStatus.OK);
    }

    @GetMapping("/addons/{id}")
    public ResponseEntity<AddonResponseDTO> getAddonById(@PathVariable Long id) {
        Optional<AddonResponseDTO> addon = addonService.getAddonById(id);
        return addon.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/addons/available")
    public ResponseEntity<List<AddonResponseDTO>> getAvailableAddons(
            @RequestParam Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate) {

        List<AddonResponseDTO> availableAddons = branchAddonService.getAvailableAddonsForDates(branchId, pickupDate, returnDate);
        return new ResponseEntity<>(availableAddons, HttpStatus.OK);
    }

}

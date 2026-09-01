package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.BranchAddonDTO;
import com.carlikeafriend_backend.backend.dto.BranchInventoryResponseDTO;
import com.carlikeafriend_backend.backend.service.IBranchAddonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carlikeafriend/")
public class InventoryController {

    private final IBranchAddonService branchAddonService;

    @Autowired
    public InventoryController(IBranchAddonService branchAddonService) {
        this.branchAddonService = branchAddonService;
    }

    // --- GESTIÓN DE INVENTARIO DE EXTRAS EN SUCURSALES ---

    // Crear/asignar inventario.
    @PostMapping("/inventory")
    public ResponseEntity<String> assignInventoryToBranch(@RequestBody @Valid BranchAddonDTO branchAddonDTO) {
        branchAddonService.assignStockToBranch(branchAddonDTO);
        return new ResponseEntity<>("Inventario físico actualizado correctamente.", HttpStatus.OK);
    }

    // Obtener inventario. Se filtra por branchId.
    @GetMapping("/inventory")
    public ResponseEntity<List<BranchInventoryResponseDTO>> getInventoryByBranch(@RequestParam Long branchId) {
        List<BranchInventoryResponseDTO> inventory = branchAddonService.getInventoryByBranchId(branchId);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }
}

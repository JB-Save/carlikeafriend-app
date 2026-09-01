package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IBranchService;
import com.carlikeafriend_backend.backend.service.IBranchTransferFeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend/")
public class BranchController {

    private final IBranchService branchService;
    private final IBranchTransferFeeService branchTransferFeeService;

    @Autowired
    public BranchController(IBranchService branchService, IBranchTransferFeeService branchTransferFeeService) {
        this.branchService = branchService;
        this.branchTransferFeeService = branchTransferFeeService;
    }

    @PostMapping("/branches")
    public ResponseEntity<SimpleResponseDTO> saveBranch(@RequestBody @Valid BranchDTO branchDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedBranch = branchService.saveBranch(branchDTO);
        return new ResponseEntity<>(savedBranch, HttpStatus.CREATED);
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchCompleteResponseDTO>> getAllBranches() {
        return new ResponseEntity<>(branchService.getAllBranches(), HttpStatus.OK);
    }

    @GetMapping("/branches/{id}")
    public ResponseEntity<BranchCompleteResponseDTO> getBranchById(@PathVariable Long id) {
        Optional<BranchCompleteResponseDTO> branchResponseDTO = branchService.getBranchById(id);
        return branchResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/branches/{id}")
    //@PatchMapping("/branches/{id}")
    public ResponseEntity<SimpleResponseDTO> updateBranch(
            @PathVariable Long id,
            @RequestBody @Valid BranchDTO branchDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO updatedBranch = branchService.updateBranch(id, branchDTO);
        return new ResponseEntity<>(updatedBranch, HttpStatus.OK);
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtiene todas las tarifas de traslado cuyo origen es la sucursal especificada.
    @GetMapping("/branches/{branchId}/transfer-fees")
    public ResponseEntity<List<BranchTransferFeeResponseDTO>> getTransferFeesByBranch(@PathVariable Long branchId) {
        List<BranchTransferFeeResponseDTO> fees = branchTransferFeeService.getBranchTransferFeeByOriginBranchId(branchId);
        return new ResponseEntity<>(fees, HttpStatus.OK);
    }

}

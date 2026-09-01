package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.BranchTransferFeeResponseDTO;
import com.carlikeafriend_backend.backend.dto.BranchTransferFeeDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
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
public class BranchTransferFeeController {

    private final IBranchTransferFeeService branchTransferFeeService;

    @Autowired
    public BranchTransferFeeController(IBranchTransferFeeService branchTransferFeeService) {
        this.branchTransferFeeService = branchTransferFeeService;
    }

    @PostMapping("/transfer-fees")
    public ResponseEntity<BranchTransferFeeResponseDTO> saveBranchTransferFee(@RequestBody @Valid BranchTransferFeeDTO branchTransferFeeDTO)
            throws DuplicateResourceException {
        BranchTransferFeeResponseDTO savedBranchTransferFee = branchTransferFeeService.saveBranchTransferFee(branchTransferFeeDTO);
        return new ResponseEntity<>(savedBranchTransferFee, HttpStatus.CREATED);
    }

    @GetMapping("/transfer-fees")
    public ResponseEntity<List<BranchTransferFeeResponseDTO>> getAllBranchTransferFees() {
        return new ResponseEntity<>(branchTransferFeeService.getAllBranchTransferFees(), HttpStatus.OK);
    }

    @GetMapping("/transfer-fees/{id}")
    public ResponseEntity<BranchTransferFeeResponseDTO> getBranchTransferFeeById(@PathVariable Long id) {
        Optional<BranchTransferFeeResponseDTO> branchTransferFeeResponseDTO = branchTransferFeeService.getBranchTransferFeeById(id);
        return branchTransferFeeResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/transfer-fees/{id}")
    //@PatchMapping("/fees/{id}")
    public ResponseEntity<BranchTransferFeeResponseDTO> updateBranchTransferFee(
            @PathVariable Long id,
            @RequestBody @Valid BranchTransferFeeDTO branchTransferFeeDTO)
            throws DuplicateResourceException {
        BranchTransferFeeResponseDTO updatedBranchTransferFee = branchTransferFeeService.updateBranchTransferFee(id, branchTransferFeeDTO);
        return new ResponseEntity<>(updatedBranchTransferFee, HttpStatus.OK);
    }

    @DeleteMapping("/transfer-fees/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchTransferFeeService.deleteBranchTransferFee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

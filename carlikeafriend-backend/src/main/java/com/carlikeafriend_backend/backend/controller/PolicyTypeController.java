package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.PolicyTypeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IPolicyTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class PolicyTypeController {

    private final IPolicyTypeService policyTypeService;

    @Autowired
    public PolicyTypeController(IPolicyTypeService policyTypeService) {
        this.policyTypeService = policyTypeService;
    }

    @PostMapping("/policy-types")
    public ResponseEntity<SimpleResponseDTO> savePolicyType(@RequestBody @Valid PolicyTypeDTO policyTypeDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedPolicyType = policyTypeService.savePolicyType(policyTypeDTO);
        return new ResponseEntity<>(savedPolicyType, HttpStatus.CREATED);
    }

    @GetMapping("/policy-types")
    public ResponseEntity<List<SimpleResponseDTO>> getAllPolicyTypes() {
        return new ResponseEntity<>(policyTypeService.getAllPolicyTypes(), HttpStatus.OK);
    }

    @GetMapping("/policy-types/{typeId}")
    public ResponseEntity<SimpleResponseDTO> getPolicyTypeById(@PathVariable Long typeId) {
        Optional<SimpleResponseDTO> policyTypeResponseDTO = policyTypeService.getPolicyTypeById(typeId);
        return policyTypeResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/policy-types/{typeId}")
    //@PatchMapping("/policy-types/{typeId}")
    public ResponseEntity<SimpleResponseDTO> updatePolicyType(
            @PathVariable Long typeId,
            @RequestBody @Valid PolicyTypeDTO policyTypeDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO updatedPolicyType = policyTypeService.updatePolicyType(typeId, policyTypeDTO);
        return new ResponseEntity<>(updatedPolicyType, HttpStatus.OK);
    }

    @DeleteMapping("/policy-types/{typeId}")
    public ResponseEntity<Void> deletePolicyType(@PathVariable Long typeId) {
        policyTypeService.deletePolicyType(typeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

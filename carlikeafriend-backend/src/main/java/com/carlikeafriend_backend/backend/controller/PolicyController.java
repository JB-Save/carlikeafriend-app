package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.PolicyCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.PolicyDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IPolicyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class PolicyController {

    private final IPolicyService policyService;

    @Autowired
    public PolicyController(IPolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/policies")
    public ResponseEntity<SimpleResponseDTO> savePolicy(@RequestBody @Valid PolicyDTO policyDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedPolicy = policyService.savePolicy(policyDTO);
        return new ResponseEntity<>(savedPolicy, HttpStatus.CREATED);
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyCompleteResponseDTO>> getAllPolicies() {
        return new ResponseEntity<>(policyService.getAllPolicies(), HttpStatus.OK);
    }

    @GetMapping("/policies/{id}")
    public ResponseEntity<PolicyCompleteResponseDTO> getPolicyById(@PathVariable Long id) {
        Optional<PolicyCompleteResponseDTO> policyResponseDTO = policyService.getPolicyById(id);
        return policyResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/policies/{id}")
    //@PatchMapping("/policies/{id}")
    public ResponseEntity<SimpleResponseDTO> updatePolicy(
            @PathVariable Long id,
            @RequestBody @Valid PolicyDTO policyDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO updatedPolicy = policyService.updatePolicy(id, policyDTO);
        return new ResponseEntity<>(updatedPolicy, HttpStatus.OK);
    }

    @DeleteMapping("/policies/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

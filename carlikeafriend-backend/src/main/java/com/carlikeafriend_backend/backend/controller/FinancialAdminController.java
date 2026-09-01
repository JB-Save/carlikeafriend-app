package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.FinancialConfigurationDTO;
import com.carlikeafriend_backend.backend.dto.FinancialConfigurationResponseDTO;
import com.carlikeafriend_backend.backend.service.IFinancialConfigurationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carlikeafriend/financial-config")
public class FinancialAdminController {

    private final IFinancialConfigurationService configService;

    @Autowired
    public FinancialAdminController(IFinancialConfigurationService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<FinancialConfigurationResponseDTO> getConfiguration() {
        return new ResponseEntity<>(configService.getConfiguration(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<FinancialConfigurationResponseDTO> updateConfiguration(@RequestBody @Valid FinancialConfigurationDTO request) {
        return new ResponseEntity<>(configService.updateConfiguration(request), HttpStatus.OK);
    }
}

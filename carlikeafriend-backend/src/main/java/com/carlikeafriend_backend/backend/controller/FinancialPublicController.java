package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.FinancialConfigurationResponseDTO;
import com.carlikeafriend_backend.backend.service.IFinancialConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend/public/financial-config")
public class FinancialPublicController {

    private final IFinancialConfigurationService configService;

    @Autowired
    public FinancialPublicController(IFinancialConfigurationService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<FinancialConfigurationResponseDTO> getPublicConfiguration() {
        Optional<FinancialConfigurationResponseDTO> response = configService.getPublicConfiguration();
        return response.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

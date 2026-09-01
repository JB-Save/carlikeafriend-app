package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.FinancialConfigurationDTO;
import com.carlikeafriend_backend.backend.dto.FinancialConfigurationResponseDTO;
import com.carlikeafriend_backend.backend.entity.FinancialConfiguration;

import java.util.Optional;

public interface IFinancialConfigurationService {
    FinancialConfigurationResponseDTO getConfiguration();
    FinancialConfigurationResponseDTO updateConfiguration(FinancialConfigurationDTO request);
    Optional<FinancialConfigurationResponseDTO> getPublicConfiguration();
}

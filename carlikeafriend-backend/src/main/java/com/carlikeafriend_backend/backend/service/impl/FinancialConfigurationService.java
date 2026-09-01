package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.FinancialConfigurationDTO;
import com.carlikeafriend_backend.backend.dto.FinancialConfigurationResponseDTO;
import com.carlikeafriend_backend.backend.entity.FinancialConfiguration;
import com.carlikeafriend_backend.backend.repository.IFinancialConfigurationRepository;
import com.carlikeafriend_backend.backend.service.IFinancialConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FinancialConfigurationService implements IFinancialConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialConfigurationService.class);
    private final IFinancialConfigurationRepository configRepository;

    @Autowired
    public FinancialConfigurationService(IFinancialConfigurationRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    @Transactional
    public FinancialConfigurationResponseDTO getConfiguration() {
        FinancialConfiguration config = configRepository.findById(1L).orElseGet(this::createDefaultConfiguration);
        return mapToDTO(config);
    }

    @Override
    @Transactional
    public FinancialConfigurationResponseDTO updateConfiguration(FinancialConfigurationDTO request) {
        FinancialConfiguration config = configRepository.findById(1L).orElseGet(this::createDefaultConfiguration);

        config.setTaxRate(request.getTaxRate());
        config.setDefaultTransferFee(request.getDefaultTransferFee());
        config.setBasicInsuranceDepositMultiplier(request.getBasicInsuranceDepositMultiplier());
        config.setPremiumInsuranceDepositMultiplier(request.getPremiumInsuranceDepositMultiplier());
        config.setFullCoverageDepositMultiplier(request.getFullCoverageDepositMultiplier());
        config.setInsuranceBasicRate(request.getInsuranceBasicRate());
        config.setInsurancePremiumRate(request.getInsurancePremiumRate());
        config.setInsuranceFullCoverageRate(request.getInsuranceFullCoverageRate());
        config.setPenaltyWindowHours(request.getPenaltyWindowHours());
        config.setCancellationPenaltyRate(request.getCancellationPenaltyRate());
        config.setNoShowPenaltyRate(request.getNoShowPenaltyRate());
        config.setMaxRentalDays(request.getMaxRentalDays());

        FinancialConfiguration savedConfig = configRepository.save(config);
        logger.info("Configuración financiera global actualizada por el Administrador.");

        return mapToDTO(savedConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialConfigurationResponseDTO> getPublicConfiguration() {
        return configRepository.findById(1L)
                .map(this::mapToDTO);
    }

    private FinancialConfiguration createDefaultConfiguration() {
        FinancialConfiguration config = new FinancialConfiguration();
        config.setId(1L);
        config.setTaxRate(0.19); // 19% IVA por defecto
        config.setDefaultTransferFee(50000.0);
        config.setBasicInsuranceDepositMultiplier(1.0);
        config.setPremiumInsuranceDepositMultiplier(0.5);
        config.setFullCoverageDepositMultiplier(0.0);
        config.setInsuranceBasicRate(10000.0);
        config.setInsurancePremiumRate(25000.0);
        config.setInsuranceFullCoverageRate(45000.0);
        config.setPenaltyWindowHours(24);
        config.setCancellationPenaltyRate(0.20);
        config.setNoShowPenaltyRate(1.0);
        config.setMaxRentalDays(30);

        return configRepository.save(config);
    }

    private FinancialConfigurationResponseDTO mapToDTO(FinancialConfiguration config){
        return new FinancialConfigurationResponseDTO(
                config.getId(),
                config.getTaxRate(),
                config.getDefaultTransferFee(),
                config.getBasicInsuranceDepositMultiplier(),
                config.getPremiumInsuranceDepositMultiplier(),
                config.getFullCoverageDepositMultiplier(),
                config.getInsuranceBasicRate(),
                config.getInsurancePremiumRate(),
                config.getInsuranceFullCoverageRate(),
                config.getPenaltyWindowHours(),
                config.getCancellationPenaltyRate(),
                config.getNoShowPenaltyRate(),
                config.getMaxRentalDays()
        );
    }
}

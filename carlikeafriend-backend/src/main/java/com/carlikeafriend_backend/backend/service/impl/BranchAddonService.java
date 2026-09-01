package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.AddonResponseDTO;
import com.carlikeafriend_backend.backend.dto.BranchAddonDTO;
import com.carlikeafriend_backend.backend.dto.BranchInventoryResponseDTO;
import com.carlikeafriend_backend.backend.dto.FinancialConfigurationResponseDTO;
import com.carlikeafriend_backend.backend.entity.Addon;
import com.carlikeafriend_backend.backend.entity.Branch;
import com.carlikeafriend_backend.backend.entity.BranchAddon;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IAddonRepository;
import com.carlikeafriend_backend.backend.repository.IBranchAddonRepository;
import com.carlikeafriend_backend.backend.repository.IBranchRepository;
import com.carlikeafriend_backend.backend.repository.IReservationExtraRepository;
import com.carlikeafriend_backend.backend.service.IBranchAddonService;
import com.carlikeafriend_backend.backend.service.IFinancialConfigurationService;
import com.carlikeafriend_backend.backend.util.DateValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchAddonService implements IBranchAddonService {

    private static final Logger logger = LoggerFactory.getLogger(BranchAddonService.class);

    private final IBranchAddonRepository branchAddonRepository;
    private final IReservationExtraRepository reservationExtraRepository;
    private final IBranchRepository branchRepository;
    private final IAddonRepository addonRepository;
    private final IFinancialConfigurationService financialConfigService;


    @Autowired
    public BranchAddonService(IBranchAddonRepository branchAddonRepository,
                              IReservationExtraRepository reservationExtraRepository,
                              IBranchRepository branchRepository,
                              IAddonRepository addonRepository,
                              IFinancialConfigurationService financialConfigService) {
        this.branchAddonRepository = branchAddonRepository;
        this.reservationExtraRepository = reservationExtraRepository;
        this.branchRepository = branchRepository;
        this.addonRepository = addonRepository;
        this.financialConfigService = financialConfigService;

    }

    @Override
    @Transactional
    public void assignStockToBranch(BranchAddonDTO branchAddonDTO) {
        logger.info("Intentando asignar {} unidades extras con ID : {} a sucursal con ID: {}.", branchAddonDTO.getTotalStock(), branchAddonDTO.getAddonId(), branchAddonDTO.getBranchId());

        // Validar que la sucursal y el extra existan
        Branch branch = branchRepository.findByIdAndDeletedFalse(branchAddonDTO.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + branchAddonDTO.getBranchId()));

        Addon addon = addonRepository.findByIdAndDeletedFalse(branchAddonDTO.getAddonId())
                .orElseThrow(() -> new ResourceNotFoundException("Extra no encontrado con ID: " + branchAddonDTO.getAddonId()));

        // Buscar si ya existe la relación, si no, crearla
        BranchAddon branchAddon = branchAddonRepository
                .findByBranchIdAndAddonId(branchAddonDTO.getBranchId(), branchAddonDTO.getAddonId())
                .orElse(new BranchAddon());

        if (branchAddon.getId() == null) {
            branchAddon.setBranch(branch);
            branchAddon.setAddon(addon);
        }

        branchAddon.setTotalStock(branchAddonDTO.getTotalStock());

        branchAddonRepository.save(branchAddon);
        logger.info("Inventario asignado: {} unidades del extra {} en la sucursal {}",
                branchAddonDTO.getTotalStock(), addon.getName(), branch.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddonResponseDTO> getAvailableAddonsForDates(Long branchId, LocalDateTime pickupDate, LocalDateTime returnDate) {

        logger.info("Calculando disponibilidad dinámica de extras para la sucursal {} entre {} y {}", branchId, pickupDate, returnDate);

        // Traemos la configuración financiera
        FinancialConfigurationResponseDTO config = financialConfigService.getConfiguration();

        DateValidationUtils.validateBookingDates(pickupDate, returnDate, config, "En Extras");

        // 1. Obtener todos los extras que FÍSICAMENTE pertenecen a esta sucursal
        List<BranchAddon> physicalStockList = branchAddonRepository.findActiveAddonsByBranchId(branchId);
        List<AddonResponseDTO> availableAddons = new ArrayList<>();

        for (BranchAddon branchAddon : physicalStockList) {
            // 2. Consulta de alto rendimiento: ¿Cuántos de este Addon específico están reservados en estas fechas?
            Integer reservedQuantity = reservationExtraRepository.sumQuantityReservedInDateRange(
                    branchId,
                    branchAddon.getAddon().getId(),
                    pickupDate,
                    returnDate
            );

            // 3. Matemática en memoria
            int availableRightNow = branchAddon.getTotalStock() - reservedQuantity;

            // 4. Si hay al menos 1 disponible, se lo enviamos al frontend
            if (availableRightNow > 0) {
                availableAddons.add(new AddonResponseDTO(
                        branchAddon.getAddon().getId(),
                        branchAddon.getAddon().getName(),
                        branchAddon.getAddon().getDescription(),
                        branchAddon.getAddon().getCurrentPrice(),
                        branchAddon.getAddon().getChargeType().toString(),
                        branchAddon.getAddon().getMaxQuantityPerReservation(),
                        branchAddon.getAddon().getMaxChargeableDays()
                ));
            }
        }

        return availableAddons;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchInventoryResponseDTO> getInventoryByBranchId(Long branchId) {
        logger.info("Obteniendo inventario físico de extras para la sucursal {}", branchId);

        List<BranchAddon> physicalStockList = branchAddonRepository.findActiveAddonsByBranchId(branchId);

        return physicalStockList.stream()
                .map(ba -> new BranchInventoryResponseDTO(
                        ba.getAddon().getId(),
                        ba.getAddon().getName(),
                        ba.getTotalStock()
                ))
                .collect(Collectors.toList());
    }
}


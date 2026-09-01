package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.BranchDTO;
import com.carlikeafriend_backend.backend.dto.BranchCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.Branch;
import com.carlikeafriend_backend.backend.entity.City;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IBranchRepository;
import com.carlikeafriend_backend.backend.repository.ICityRepository;
import com.carlikeafriend_backend.backend.service.IBranchService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchService implements IBranchService {

    private static final Logger logger = LoggerFactory.getLogger(BranchService.class);

    private final IBranchRepository branchRepository;
    private final ICityRepository cityRepository;

    @Autowired
    public BranchService(IBranchRepository branchRepository, ICityRepository cityRepository) {
        this.branchRepository = branchRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public SimpleResponseDTO saveBranch(BranchDTO branchDTO) throws DuplicateResourceException {

        String branchName = StringUtils.capitalize(branchDTO.getName());

        logger.info("Intentando guardar nueva sucursal: {}", branchName);

        //Validación de duplicados por nombre
        if (branchRepository.existsByNameAndDeletedFalse(branchName)) {
            logger.warn("Ya existe una sucursal activa con el nombre: {}", branchName);
            throw new DuplicateResourceException("Ya existe una sucursal activa con el nombre: " + branchName);
        }

        //Mapear DTO a Entidad
        Branch branch = new Branch();
        branch.setName(branchName);
        branch.setAddress(branchDTO.getAddress());
        branch.setLatitude(branchDTO.getLatitude());
        branch.setLongitude(branchDTO.getLongitude());

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN
        updateCityAssociation(branch, branchDTO.getCityId());


        Branch savedBranch = branchRepository.save(branch);
        logger.info("Sucursal guardada exitosamente con ID: {}", savedBranch.getId());
        return mapToBranchDto(savedBranch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchCompleteResponseDTO> getAllBranches() {
        logger.info("Buscando todas las sucursales.");
        return branchRepository.findAllByDeletedFalse().stream()
                .map(this::mapToBranchCompleteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchCompleteResponseDTO> getBranchById(Long id) {
        logger.info("Buscando sucursal con ID: {}", id);
        return branchRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToBranchCompleteDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updateBranch(Long id, BranchDTO branchDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar sucursal con ID: {}", id);

        String branchName = StringUtils.capitalize(branchDTO.getName());

        // Buscamos solo si no está borrada
        Branch existingBranch = branchRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Sucursal no encontrada con ID: " + id));

        // Validar que el nombre de la sucursal sea único entre las activas, excluyendo la característica actual
        if (branchName != null && !branchName.equals(StringUtils.capitalize(existingBranch.getName()))) {
            if (branchRepository.existsByNameAndIdNotAndDeletedFalse(branchName, id)) {
                throw new DuplicateResourceException("El nombre " + branchName + " ya está en uso por otra sucursal activa.");
            }
            existingBranch.setName(branchName);
        }

        // Actualizar datos básicos de la característica
        Optional.ofNullable(branchDTO.getAddress()).ifPresent(existingBranch::setAddress);
        Optional.ofNullable(branchDTO.getLatitude()).ifPresent(existingBranch::setLatitude);
        Optional.ofNullable(branchDTO.getLongitude()).ifPresent(existingBranch::setLongitude);

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN (Limpia y reasigna)
        updateCityAssociation(existingBranch, branchDTO.getCityId());

        Branch updatedBranch = branchRepository.save(existingBranch);
        return mapToBranchDto(updatedBranch);
    }

    @Override
    @Transactional
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));

        if (branch.hasPendingReservations()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen reservas activas asociados a esta sucursal.");
        }

        if (branch.hasActiveVehicles()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen vehículos activos asociados a esta sucursal.");
        }

        // 1. LIMPIEZA FÍSICA DE TARIFAS (Orphan Removal)
        // Usamos copias de las listas para evitar ConcurrentModificationException
        new ArrayList<>(branch.getOutgoingTransferFees()).forEach(fee -> {
            if (fee.getDestinationBranch() != null) {
                fee.getDestinationBranch().removeIncomingTransferFee(fee);
            }
            branch.removeOutgoingTransferFee(fee);
        });

        new ArrayList<>(branch.getIncomingTransferFees()).forEach(fee -> {
            if (fee.getOriginBranch() != null) {
                fee.getOriginBranch().removeOutgoingTransferFee(fee);
            }
            branch.removeIncomingTransferFee(fee);
        });

        // 2. LIBERAR EL NOMBRE (Renombramiento estratégico)
        // Esto permite que el nombre original pueda ser usado de nuevo en otra sucursal
        String timestamp = String.valueOf(System.currentTimeMillis());
        branch.setName(branch.getName() + "_DELETED_" + timestamp);

        // 3. BORRADO LÓGICO
        branch.setDeleted(true);

        branchRepository.save(branch);
        logger.warn("Sucursal con ID {} borrada lógicamente.", id);

    }


    private void updateCityAssociation(Branch branch, Long cityId) {
        if (cityId == null) return;

        // Desvincular de la ciudad anterior (si existe)
        if (branch.getCity() != null) {
            branch.getCity().removeBranch(branch);
        }

        // Vincular nueva
        City newCity = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada con ID: " + cityId));

        newCity.addBranch(branch); // Método de conveniencia de City
    }

    private SimpleResponseDTO mapToBranchDto(Branch branch) {
        return new SimpleResponseDTO(
                branch.getId(),
                branch.getName()
        );
    }

    private BranchCompleteResponseDTO mapToBranchCompleteDto(Branch branch) {

        SimpleResponseDTO cityResponseDTO = branch.getCity() != null
                ? new SimpleResponseDTO(branch.getCity().getId(), branch.getCity().getName())
                : null;
        return new BranchCompleteResponseDTO(
                branch.getId(),
                branch.getName(),
                branch.getAddress(),
                cityResponseDTO,
                branch.getLatitude(),
                branch.getLongitude()
        );
    }


}

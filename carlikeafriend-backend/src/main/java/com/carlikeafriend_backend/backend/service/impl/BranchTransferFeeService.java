package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.BranchTransferFeeDTO;
import com.carlikeafriend_backend.backend.dto.BranchTransferFeeResponseDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.Branch;
import com.carlikeafriend_backend.backend.entity.BranchTransferFee;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.InvalidRangeException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IBranchRepository;
import com.carlikeafriend_backend.backend.repository.IBranchTransferFeeRepository;
import com.carlikeafriend_backend.backend.service.IBranchTransferFeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchTransferFeeService implements IBranchTransferFeeService {

    private static final Logger logger = LoggerFactory.getLogger(BranchTransferFeeService.class);

    private final IBranchTransferFeeRepository branchTransferFeeRepository;
    private final IBranchRepository branchRepository;

    @Autowired
    public BranchTransferFeeService(IBranchTransferFeeRepository branchTransferFeeRepository, IBranchRepository branchRepository) {
        this.branchTransferFeeRepository = branchTransferFeeRepository;
        this.branchRepository = branchRepository;
    }


    @Override
    @Transactional
    public BranchTransferFeeResponseDTO saveBranchTransferFee(BranchTransferFeeDTO branchTransferFeeDTO) throws DuplicateResourceException {

        if (branchTransferFeeDTO.getOriginBranchId().equals(branchTransferFeeDTO.getDestinationBranchId())) {
            throw new InvalidRangeException("La sucursal de origen y destino no pueden ser la misma.");
        }

        if (branchTransferFeeRepository.existsByOriginBranchIdAndDestinationBranchId(branchTransferFeeDTO.getOriginBranchId(), branchTransferFeeDTO.getDestinationBranchId())) {
            logger.info("La tarifa de transferencia de sucursal ya exite con IDs: {}, {}", branchTransferFeeDTO.getOriginBranchId(), branchTransferFeeDTO.getDestinationBranchId());
            throw new DuplicateResourceException("La tarifa de transferencia de sucursal ya exite");
        }

        // Mapear DTO a Entidad
        BranchTransferFee branchTransferFee = new BranchTransferFee();
        branchTransferFee.setFeeAmount(branchTransferFeeDTO.getFeeAmount());

        // 1. OBTENER AMBAS SUCURSALES PRIMERO (Esto evita el auto-flush prematuro)
        Branch origin = branchRepository.findByIdAndDeletedFalse(branchTransferFeeDTO.getOriginBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal de origen no encontrada con ID: " + branchTransferFeeDTO.getOriginBranchId()));

        Branch destination = branchRepository.findByIdAndDeletedFalse(branchTransferFeeDTO.getDestinationBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal de destino no encontrada con ID: " + branchTransferFeeDTO.getDestinationBranchId()));

        // 2. ASIGNACIÓN AL LADO DUEÑO
        branchTransferFee.setOriginBranch(origin);
        branchTransferFee.setDestinationBranch(destination);

        // 3. SINCRONIZAR COLECCIONES
        origin.addOutgoingTransferFee(branchTransferFee);
        destination.addIncomingTransferFee(branchTransferFee);

        BranchTransferFee savedBranchTransferFee = branchTransferFeeRepository.save(branchTransferFee);
        logger.info("Tarifa de transferencia de sucursal guardada exitosamente con ID: {}", savedBranchTransferFee.getId());

        return mapToDto(savedBranchTransferFee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchTransferFeeResponseDTO> getAllBranchTransferFees() {
        logger.info("Buscando todas las tarifa de transferencia de sucursal");
        return branchTransferFeeRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchTransferFeeResponseDTO> getBranchTransferFeeById(Long id) {
        logger.info("Buscando tarifa de transferencia de sucursal con ID: {}", id);
        return branchTransferFeeRepository.findById(id)
                .map(this::mapToDto);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BranchTransferFeeResponseDTO> getBranchTransferFeeByOriginBranchId(Long branchId) {
        logger.info("Obteniendo tarifas de transferencia para la sucursal {}", branchId);

        List<BranchTransferFee> branchFeeList = branchTransferFeeRepository.findActiveFeesByOriginBranchId(branchId);

        return branchFeeList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BranchTransferFeeResponseDTO updateBranchTransferFee(Long id, BranchTransferFeeDTO branchTransferFeeDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar tarifa de transferencia de sucursal con ID: {}", id);

        // 1. Validaciones Iniciales
        if (branchTransferFeeDTO.getOriginBranchId().equals(branchTransferFeeDTO.getDestinationBranchId())) {
            throw new InvalidRangeException("La sucursal de origen y destino no pueden ser la misma.");
        }
        // Buscar la tarifa existente
        BranchTransferFee existingBranchTransferFee = branchTransferFeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada con ID: " + id));

        // 2. Determinar qué relaciones han cambiado realmente
        boolean isChangingOrigin = branchTransferFeeDTO.getOriginBranchId() != null && !branchTransferFeeDTO.getOriginBranchId().equals(existingBranchTransferFee.getOriginBranch().getId());
        boolean isChangingDestination = branchTransferFeeDTO.getDestinationBranchId() != null && !branchTransferFeeDTO.getDestinationBranchId().equals(existingBranchTransferFee.getDestinationBranch().getId());

        // 3. Validar que las sucursales de transferencia sean únicos, excluyendo la sucursal transferencia actual
        if (isChangingOrigin || isChangingDestination) {
            if (branchTransferFeeRepository.existsByOriginBranchIdAndDestinationBranchIdAndIdNot(
                    branchTransferFeeDTO.getOriginBranchId(), branchTransferFeeDTO.getDestinationBranchId(), id)) {
                throw new DuplicateResourceException("Ya existe una tarifa de transferencia configurada para estas sucursales..");
            }
        }

        // 4. Buscamos todo antes de mutar
        Branch newOrigin = null;
        if (isChangingOrigin) {
            newOrigin = branchRepository.findByIdAndDeletedFalse(branchTransferFeeDTO.getOriginBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal origen no encontrada: " + branchTransferFeeDTO.getOriginBranchId()));
        }

        Branch newDestination = null;
        if (isChangingDestination) {
            newDestination = branchRepository.findByIdAndDeletedFalse(branchTransferFeeDTO.getDestinationBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal destino no encontrada: " + branchTransferFeeDTO.getDestinationBranchId()));
        }

        // 5. Mutación (Usando tus métodos auxiliares seguros)
        if (isChangingOrigin) {
            updateOriginBranchAssociation(existingBranchTransferFee, newOrigin);
        }

        if (isChangingDestination) {
            updateDestinationBranchAssociation(existingBranchTransferFee, newDestination);
        }

        Optional.ofNullable(branchTransferFeeDTO.getFeeAmount()).ifPresent(existingBranchTransferFee::setFeeAmount);

        BranchTransferFee updatedBranchTransferFee = branchTransferFeeRepository.save(existingBranchTransferFee);

        return mapToDto(updatedBranchTransferFee);
    }

    @Override
    @Transactional
    public void deleteBranchTransferFee(Long id) {
        BranchTransferFee branchTransferFee = branchTransferFeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa de transferencia de sucursal no encontrada con ID: " + id));


        // LIMPIEZA DE MEMORIA: Desvincular antes de borrar
        // 1. Limpiar Sucursal de Origen
        if (branchTransferFee.getOriginBranch() != null) {
            branchTransferFee.getOriginBranch().removeOutgoingTransferFee(branchTransferFee);
        }

        // 2. Limpiar Sucursal de destino
        if (branchTransferFee.getDestinationBranch() != null) {
            branchTransferFee.getDestinationBranch().removeIncomingTransferFee(branchTransferFee);
        }

        branchTransferFeeRepository.delete(branchTransferFee);
        logger.warn("Tarifa de transferencia de sucursal eliminada con ID: {}", id);
    }

    private void updateOriginBranchAssociation(BranchTransferFee branchFee, Branch newOrigin) {
        if (newOrigin == null) return;

        Branch oldOrigin = branchFee.getOriginBranch();

        // Desvincular de la sucursal antigua si existía
        if (oldOrigin != null) {
            oldOrigin.removeOutgoingTransferFee(branchFee);
        }

        // Asignación directa al lado dueño
        branchFee.setOriginBranch(newOrigin);

        // Vincular a la nueva sucursal
        newOrigin.addOutgoingTransferFee(branchFee);
    }

    private void updateDestinationBranchAssociation(BranchTransferFee branchFee, Branch newDestination) {
        if (newDestination == null) return;

        Branch oldDestination = branchFee.getDestinationBranch();

        // Desvincular de la sucursal antigua
        if (oldDestination != null) {
            oldDestination.removeIncomingTransferFee(branchFee);
        }

        // Asignación directa
        branchFee.setDestinationBranch(newDestination);

        // Vincular a la nueva
        newDestination.addIncomingTransferFee(branchFee);
    }

    private BranchTransferFeeResponseDTO mapToDto(BranchTransferFee branchTransferFee) {
        SimpleResponseDTO originBranch = branchTransferFee.getOriginBranch() != null
                ? new SimpleResponseDTO(branchTransferFee.getOriginBranch().getId(), branchTransferFee.getOriginBranch().getName())
                : null;

        SimpleResponseDTO destinationBranch = branchTransferFee.getDestinationBranch() != null
                ? new SimpleResponseDTO(branchTransferFee.getDestinationBranch().getId(), branchTransferFee.getDestinationBranch().getName())
                : null;

        return new BranchTransferFeeResponseDTO(
                branchTransferFee.getId(),
                originBranch,
                destinationBranch,
                branchTransferFee.getFeeAmount()
        );

    }
}

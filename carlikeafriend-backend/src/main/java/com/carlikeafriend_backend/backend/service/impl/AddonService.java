package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.AddonDTO;
import com.carlikeafriend_backend.backend.dto.AddonResponseDTO;
import com.carlikeafriend_backend.backend.entity.Addon;
import com.carlikeafriend_backend.backend.entity.ChargeType;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IAddonRepository;
import com.carlikeafriend_backend.backend.service.IAddonService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddonService implements IAddonService {

    private static final Logger logger = LoggerFactory.getLogger(AddonService.class);
    private final IAddonRepository addonRepository;

    @Autowired
    public AddonService(IAddonRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    @Override
    @Transactional
    public AddonResponseDTO createAddon(AddonDTO addonDTO) throws DuplicateResourceException{

       String addonName = StringUtils.capitalize(addonDTO.getName());

        logger.info("Intentando guardar nuevo extra: {}", addonName);

        if (addonRepository.existsByNameAndDeletedFalse(addonName)) {
            logger.warn("Ya existe un extra activo con el nombre: {}", addonName);
            throw new DuplicateResourceException("Ya existe un extra activo con el nombre: " + addonName);
        }

        ChargeType chargeType = ChargeType.validate(addonDTO.getChargeType());

        Addon addon = new Addon();
        addon.setName(addonName);
        addon.setDescription(addonDTO.getDescription());
        addon.setCurrentPrice(addonDTO.getCurrentPrice());
        addon.setChargeType(chargeType);
        addon.setMaxQuantityPerReservation(addonDTO.getMaxQuantityPerReservation());
        addon.setMaxChargeableDays(addonDTO.getMaxChargeableDays());

        Addon savedAddon = addonRepository.save(addon);
        logger.info("Extra creado exitosamente: {}", savedAddon.getName());
        return mapToAddonDTO(savedAddon);
    }

    @Override
    @Transactional
    public AddonResponseDTO updateAddon(Long id, AddonDTO addonDTO) throws DuplicateResourceException{
        logger.info("Intentando actualizar extra con ID: {}", id);

        java.lang.String addonName = StringUtils.capitalize(addonDTO.getName());

        Addon existingAddon = addonRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Extra no encontrado con ID: " + id));

        if (addonName != null && !addonName.equals(StringUtils.capitalize(existingAddon.getName()))) {
            if (addonRepository.existsByNameAndIdNotAndDeletedFalse(addonName, id)) {
                throw new DuplicateResourceException("El nombre " + addonName + " ya está en uso por otro extra activo.");
            }
            existingAddon.setName(addonName);
        }

        Optional.ofNullable(addonDTO.getDescription()).ifPresent(existingAddon::setDescription);
        Optional.ofNullable(addonDTO.getCurrentPrice()).ifPresent(existingAddon::setCurrentPrice);
        Optional.ofNullable(addonDTO.getChargeType()).ifPresent(ct -> existingAddon.setChargeType(ChargeType.validate(ct)));
        Optional.ofNullable(addonDTO.getMaxQuantityPerReservation()).ifPresent(existingAddon::setMaxQuantityPerReservation);
        Optional.ofNullable(addonDTO.getMaxChargeableDays()).ifPresent(existingAddon::setMaxChargeableDays);

        Addon updatedAddon = addonRepository.save(existingAddon);
        return mapToAddonDTO(updatedAddon);
    }

    @Override
    @Transactional
    public void deleteAddon(Long id) {
        Addon addon = addonRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extra no encontrado con ID: " + id));

        // Borrado lógico. Automáticamente se dejará de mostrar en el frontend
        java.lang.String timestamp = java.lang.String.valueOf(System.currentTimeMillis());
        addon.setName(addon.getName() + "_DELETED_" + timestamp);
        addon.setDeleted(true);

        addonRepository.save(addon);
        logger.info("Extra eliminado (lógicamente) ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddonResponseDTO> getAllAddons() {
        logger.info("Buscando todos los Extras.");
        return addonRepository.findAllByDeletedFalse().stream()
                .map(this::mapToAddonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AddonResponseDTO> getAddonById(Long id) {
        logger.info("Buscando extra con ID: {}", id);
        return addonRepository.findByIdAndDeletedFalse(id).map(this::mapToAddonDTO);
    }

    private AddonResponseDTO mapToAddonDTO(Addon addon) {
        return new AddonResponseDTO(
                addon.getId(),
                addon.getName(),
                addon.getDescription(),
                addon.getCurrentPrice(),
                addon.getChargeType().toString(),
                addon.getMaxQuantityPerReservation(),
                addon.getMaxChargeableDays()
        );
    }

}

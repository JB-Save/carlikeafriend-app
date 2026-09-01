package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.AddonDTO;
import com.carlikeafriend_backend.backend.dto.AddonResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IAddonService {
    AddonResponseDTO createAddon(AddonDTO addonDTO) throws DuplicateResourceException;
    AddonResponseDTO updateAddon(Long id, AddonDTO addonDTO) throws DuplicateResourceException;
    void deleteAddon(Long id); // Borrado lógico
    List<AddonResponseDTO> getAllAddons();
    Optional<AddonResponseDTO> getAddonById(Long id);
}

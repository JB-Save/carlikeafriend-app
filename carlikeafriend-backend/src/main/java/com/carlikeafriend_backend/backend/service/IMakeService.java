package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.MakeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IMakeService {

    SimpleResponseDTO saveMake(MakeDTO makeDTO) throws DuplicateResourceException;

    List<SimpleResponseDTO> getAllMakes();

    Optional<SimpleResponseDTO> getMakeById(Long id);

    SimpleResponseDTO updateMake(Long id, MakeDTO makeDTO) throws DuplicateResourceException;

    void deleteMake(Long id);
}

package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.MakeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.Make;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IMakeRepository;
import com.carlikeafriend_backend.backend.service.IMakeService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MakeService implements IMakeService {

    private static final Logger logger = LoggerFactory.getLogger(MakeService.class);

    private final IMakeRepository makeRepository;

    @Autowired
    public MakeService(IMakeRepository makeRepository) {
        this.makeRepository = makeRepository;
    }

    @Override
    @Transactional
    public SimpleResponseDTO saveMake(MakeDTO makeDTO) throws DuplicateResourceException {

        String makeName = StringUtils.capitalize(makeDTO.getName());

        logger.info("Intentando guardar nueva marca: {}", makeName);

        // Validación de duplicados por nombre
        if (makeRepository.existsByNameAndDeletedFalse(makeName)) {
            logger.warn("Ya existe una marca activa con el nombre: {}", makeName);
            throw new DuplicateResourceException("Ya existe una marca activa con el nombre: " + makeName);
        }

        // Mapear DTO a Entidad
        Make make = new Make();
        make.setName(makeName);

        Make savedMake = makeRepository.save(make);
        logger.info("Marca guardada exitosamente con ID: {}", savedMake.getId());
        return mapToMakeDto(savedMake);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleResponseDTO> getAllMakes() {
        logger.info("Buscando todos las marcas");
        return makeRepository.findAllByDeletedFalse().stream()
                .map(this::mapToMakeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimpleResponseDTO> getMakeById(Long id) {
        logger.info("Buscando marca con ID: {}", id);
        return makeRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToMakeDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updateMake(Long id, MakeDTO makeDTO) throws DuplicateResourceException {

        logger.info("Intentando actualizar marca con ID: {}", id);

        String makeName = StringUtils.capitalize(makeDTO.getName());

        Make existingMake = makeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Marca no encontrada con ID: " + id));

        // Validar que el nombre de la marca activa sea único, excluyendo la marca actual
        if (makeName != null && !makeName.equals(StringUtils.capitalize(existingMake.getName()))) {
            if (makeRepository.existsByNameAndIdNotAndDeletedFalse(makeName, id)) {
                throw new DuplicateResourceException("El nombre " + makeName + " ya está en uso por otra marca activa.");
            }
            //Actualizar datos básicos de la marca
            existingMake.setName(makeName);
        }

        Make updatedMake = makeRepository.save(existingMake);
        return mapToMakeDto(updatedMake);
    }

    @Override
    @Transactional
    public void deleteMake(Long id) {
        Make make = makeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + id));

        if (make.hasActiveProducts()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen productos activos asociadas a esta marca.");
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        make.setName(make.getName() + "_DELETED_" + timestamp);

        make.setDeleted(true);

        makeRepository.save(make);
        logger.warn("Marca ID {} borrada lógicamente. Nombre modificado para liberar restricción única.", id);

    }

    private SimpleResponseDTO mapToMakeDto(Make make) {
        return new SimpleResponseDTO(
                make.getId(),
                make.getName()
        );
    }
}

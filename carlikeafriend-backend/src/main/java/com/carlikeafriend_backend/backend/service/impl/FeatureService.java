package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.Feature;
import com.carlikeafriend_backend.backend.entity.Icon;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.IFeatureIconRepository;
import com.carlikeafriend_backend.backend.repository.IFeatureRepository;
import com.carlikeafriend_backend.backend.service.IFeatureService;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.util.FileValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeatureService implements IFeatureService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

    private final IFeatureRepository featureRepository;
    private final IFileStorageService fileStorageService;
    private final IFeatureIconRepository featureIconRepository;
    private final FileValidationUtils fileValidationUtils; // Inyección de utilidad
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public FeatureService(IFeatureRepository featureRepository,
                          IFileStorageService fileStorageService,
                          IFeatureIconRepository featureIconRepository,
                          FileValidationUtils fileValidationUtils,
                          ApplicationEventPublisher eventPublisher) {
        this.featureRepository = featureRepository;
        this.fileStorageService = fileStorageService;
        this.featureIconRepository = featureIconRepository;
        this.fileValidationUtils = fileValidationUtils;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public FeatureResponseDTO saveFeature(FeatureDTO featureDTO, MultipartFile imageFile) throws
            UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException {

        logger.info("Intentando guardar nueva característica: {}", featureDTO.getName());

        // Validación de duplicados por nombre
        if (featureRepository.existsByName(featureDTO.getName())) {
            logger.warn("El nombre de la característica ya existe: {}", featureDTO.getName());
            throw new UniqueNameException("El nombre de la caraterística ya existe.");
        }

        // Validación de archivos usando la utilidad
        fileValidationUtils.validateAtLeastOneImage(imageFile);
        fileValidationUtils.validateImageFile(imageFile);

        // Mapear DTO a Entidad
        Feature feature = new Feature();
        feature.setName(featureDTO.getName());

        // Procesar Imagen
        if (imageFile.getSize() > 0 && !imageFile.isEmpty()) {
            String imagePath = fileStorageService.storeFile(imageFile, "feature_folder");
            Icon icon = new Icon();
            icon.setImagePath(imagePath);
            icon.setOriginalName(imageFile.getOriginalFilename());
            icon.setContentType(imageFile.getContentType());
            feature.setIcon(icon);
        }

        Feature savedFeature = featureRepository.save(feature);
        logger.info("Característica guardada exitosamente con ID: {}", savedFeature.getId());
        return convertToDto(savedFeature);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeatureResponseDTO> findAllFeatures() {
        logger.info("Buscando todas las características.");
        return featureRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeatureResponseDTO> findFeatureById(Long id) {
        logger.info("Buscando característica con ID: {}", id);
        return featureRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public FeatureResponseDTO updateFeature(Long id, FeatureDTO featureDTO, MultipartFile newImageFile) throws UniqueNameException, InvalidFileExtensionException, IOException {

        logger.info("Intentando actualizar característica con ID: {}", id);

        Feature existingFeature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id));

        // Validar que el nombre de la característica sea único, excluyendo la característica actual
        if (featureDTO.getName() != null && !featureDTO.getName().equals(existingFeature.getName()) && featureRepository.existsByNameAndIdNot(featureDTO.getName(), id)) {
            throw new UniqueNameException("El nombre de la característica ya existe.");
        }

        // Actualizar datos básicos de la característica
        Optional.ofNullable(featureDTO.getName()).ifPresent(existingFeature::setName);

        // Validación de nueva imagen
        if (newImageFile != null) {
            fileValidationUtils.validateImageFile(newImageFile);

            // Eliminar la imagen antigua si existe
            if (existingFeature.getIcon() != null) {
                // Solo agendamos el borrado para DESPUÉS de que se guarde todo bien
                eventPublisher.publishEvent(new ImageDeletedEvent(existingFeature.getIcon().getImagePath()));
            }
            // Guardar la nueva imagen
            String imagePath = fileStorageService.storeFile(newImageFile, "feature_folder");
            Icon icon = new Icon();
            icon.setImagePath(imagePath);
            icon.setOriginalName(newImageFile.getOriginalFilename());
            icon.setContentType(newImageFile.getContentType());
            existingFeature.setIcon(icon);
        }

        Feature updatedFeature = featureRepository.save(existingFeature);
        return convertToDto(updatedFeature);

    }

    @Override
    @Transactional
    public void deleteFeature(Long id) throws IOException {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id));

        // Guardar la ruta temporalmente
        String pathToDelete = (feature.getIcon() != null) ? feature.getIcon().getImagePath() : null;

        // Intentar borrar de la DB.
        // Si hay una FK Constraint (DataIntegrityViolationException), explota aquí
        // y NUNCA llega a la línea del evento.
        featureRepository.delete(feature);

        // Si llegamos aquí, la DB aceptó la orden (aunque aún no ha hecho commit).
        // Publicamos el evento. Spring lo guardará y solo lo ejecutará si el commit final funciona.
        if (pathToDelete != null) {
            eventPublisher.publishEvent(new ImageDeletedEvent(pathToDelete));
        }
        logger.warn("Característica marcada para eliminación con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFeatureImageContentTypeByImagePath(String imagePath) {
        return featureIconRepository.findContentTypeByImagePath(imagePath)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta de imagen no encontrada"));
    }


    private FeatureResponseDTO convertToDto(Feature feature) {
        ImageDTO featureIconDto = null;
        if (feature.getIcon() != null) {
            featureIconDto = new ImageDTO();
            featureIconDto.setId(feature.getIcon().getId());
            featureIconDto.setImagePath(feature.getIcon().getImagePath());
            featureIconDto.setOriginalName(feature.getIcon().getOriginalName());
            featureIconDto.setContentType(feature.getIcon().getContentType());
        }
        return new FeatureResponseDTO(
                feature.getId(),
                feature.getName(),
                featureIconDto
        );
    }
}

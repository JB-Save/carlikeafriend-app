package com.carlikeafriend_backend.backend.service;


import com.carlikeafriend_backend.backend.dto.FeatureDTO;
import com.carlikeafriend_backend.backend.dto.FeatureResponseDTO;
import com.carlikeafriend_backend.backend.entity.Feature;
import com.carlikeafriend_backend.backend.entity.Icon;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.repository.IFeatureRepository;
import com.carlikeafriend_backend.backend.service.impl.FeatureService;
import com.carlikeafriend_backend.backend.service.impl.FileStorageService;
import com.carlikeafriend_backend.backend.util.FileValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private IFeatureRepository featureRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private FileValidationUtils fileValidationUtils;

    @InjectMocks
    private FeatureService featureService;

    private Feature existingFeature;
    private FeatureDTO updateDTO;

    @BeforeEach
    void setUp() {
        existingFeature = new Feature();
        existingFeature.setId(1L);
        existingFeature.setName("Antigua");
        existingFeature.setDeleted(false);

        Icon img = new Icon();
        img.setImagePath("/image/feature_folder/old.jpg");
        existingFeature.setIcon(img);

        updateDTO = new FeatureDTO();
        updateDTO.setName("Nueva");
    }

    @Test
    @DisplayName("Crear característica: Lanza excepción si el archivo es inválido")
    void createFeature_InvalidFile_StopsExecution() throws Exception {
        MultipartFile mockFile = new MockMultipartFile("invalid", "test.txt", "text/plain", "content".getBytes());
        doThrow(new InvalidFileExtensionException("No permitido"))
                .when(fileValidationUtils).validateImageFile(mockFile);

        assertThrows(InvalidFileExtensionException.class, () ->
                featureService.saveFeature(updateDTO, mockFile)
        );
        verify(featureRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar característica: Solo texto, mantiene imagen anterior")
    void updateFeature_OnlyText_KeepOldImage() throws IOException {
        when(featureRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingFeature));
        when(featureRepository.save(any(Feature.class))).thenAnswer(i -> i.getArguments()[0]);

        FeatureResponseDTO result = featureService.updateFeature(1L, updateDTO, null);

        assertEquals("Nueva", result.getName());
        assertEquals("/image/feature_folder/old.jpg", result.getIcon().getImagePath());
        verify(fileStorageService, never()).storeFile(any(), any());
    }

    @Test
    @DisplayName("Lanza DuplicateResourceException si el nombre ya existe al actualizar")
    void updateFeature_DuplicateName_ThrowsException() {
        when(featureRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingFeature));
        when(featureRepository.existsByNameAndIdNotAndDeletedFalse(anyString(), anyLong())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                featureService.updateFeature(1L, updateDTO, any())
        );
    }

    @Test
    @DisplayName("Eliminar característica: Verifica borrado lógico y mutación del nombre")
    void deleteFeature_LogicalDelete() throws IOException {
        when(featureRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingFeature));

        featureService.deleteFeature(1L);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(featureRepository).save(captor.capture());

        Feature saved = captor.getValue();
        assertTrue(saved.isDeleted());
        assertTrue(saved.getName().contains("_DELETED_"));
    }

    @Test
    @DisplayName("Actualizar característica: Con nueva imagen, reemplaza la anterior")
    void updateFeature_WithNewImage_ReplacesOldOne() throws IOException {

        when(featureRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingFeature));
        MultipartFile mockFile = new MockMultipartFile("newImage", "newImage.jpg", "image/jpeg", "some-image-bytes".getBytes());

        // El servicio llama a validateImageFile, como es un mock de void no hace nada (comportamiento deseado)
        doNothing().when(fileValidationUtils).validateImageFile(mockFile);

        when(fileStorageService.storeFile(eq(mockFile), anyString())).thenReturn("/image/feature_folder/newImage.jpg");
        when(featureRepository.save(any(Feature.class))).thenAnswer(i -> i.getArguments()[0]);


        FeatureResponseDTO result = featureService.updateFeature(1L, updateDTO, mockFile);

        assertEquals("/image/feature_folder/newImage.jpg", result.getIcon().getImagePath());
        verify(eventPublisher).publishEvent(new ImageDeletedEvent(any()));
        verify(fileStorageService).storeFile(mockFile, "feature_folder");
        verify(fileValidationUtils).validateImageFile(mockFile); // Verifica que se validó el archivo
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException al actualizar característica inexistente")
    void updateFeature_NotFound_ThrowsException() {
        when(featureRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                featureService.updateFeature(99L, updateDTO, null)
        );
        verify(featureRepository, never()).save(any());
    }

    @Test
    @DisplayName("Uso de ArgumentCaptor para verificar campos persistidos")
    void updateFeature_VerifyPersistenceWithCaptor() throws IOException {
        when(featureRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingFeature));
        when(featureRepository.save(any(Feature.class))).thenReturn(existingFeature);

        featureService.updateFeature(1L, updateDTO, null);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(featureRepository).save(captor.capture());

        Feature saved = captor.getValue();
        assertEquals("Nueva", saved.getName());
    }


}
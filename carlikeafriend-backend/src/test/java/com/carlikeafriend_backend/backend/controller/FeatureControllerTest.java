package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.FeatureDTO;
import com.carlikeafriend_backend.backend.dto.FeatureResponseDTO;
import com.carlikeafriend_backend.backend.dto.ImageDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IFeatureService;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeatureController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula un usuario autenticado para todos los tests
class FeatureControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IFeatureService featureService;

    @MockitoBean
    private IFileStorageService fileStorageService;

    // --- Mocks necesarios para que el filtro de seguridad JWT no falle ---
    @MockitoBean
    private IJwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;
    // ---------------------------------------------------------------------

    private FeatureDTO featureDTO;
    private FeatureResponseDTO featureResponseDTO;
    private ImageDTO imageDTO;

    @BeforeEach
    void setUp() {
        featureDTO = new FeatureDTO();
        featureDTO.setName("Aire Acondiciondao");
        imageDTO = new ImageDTO();
        imageDTO.setId(10L);
        imageDTO.setImagePath("/image/feature_folder/test.jpg");
        imageDTO.setOriginalName("test.jpg");
        imageDTO.setContentType("image/jpeg");


        featureResponseDTO = new FeatureResponseDTO(
                1L,
                "Aire Acondicionado",
                imageDTO
        );
    }

    @Test
    @DisplayName("GET /features - Obtener todas las características exitosamente")
    void getAllFeatures_Success() throws Exception {
        when(featureService.getAllFeatures()).thenReturn(List.of(featureResponseDTO));

        mockMvc.perform(get("/carlikeafriend/features"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aire Acondicionado"));
    }

    @Test
    @DisplayName("GET /features/{id} - Devuelve 404 si la característica no existe")
    void getFeatureById_NotFound() throws Exception {
        when(featureService.getFeatureById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carlikeafriend/features/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /features - Devuelve 409 Conflict al duplicar nombre")
    void createFeature_DuplicateName_Returns409() throws Exception {
        MockMultipartFile featurePart = new MockMultipartFile(
                "feature", "", "application/json", objectMapper.writeValueAsBytes(featureDTO));

        when(featureService.saveFeature(any(), any()))
                .thenThrow(new DuplicateResourceException("Ya existe"));

        mockMvc.perform(multipart("/carlikeafriend/features")
                        .file(featurePart)
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /features - Crear caracterísitca con imagen exitosamente")
    void createFeature_Success() throws Exception {
        MockMultipartFile featurePart = new MockMultipartFile(
                "feature",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(featureDTO)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(featureService.saveFeature(any(), any())).thenReturn(featureResponseDTO);

        // Añadimos .with(csrf()) porque es una petición que modifica datos
        mockMvc.perform(multipart("/carlikeafriend/features")
                        .file(featurePart)
                        .file(imagePart)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /features/{id} - Debería actualizar característica e imagen")
    void updateFeature_Success() throws Exception {
        MockMultipartFile featurePart = new MockMultipartFile("feature", "", "application/json",
                objectMapper.writeValueAsBytes(featureDTO));

        when(featureService.updateFeature(eq(1L), any(FeatureDTO.class), any()))
                .thenReturn(featureResponseDTO);

        // Simulamos PUT usando multipart y la cabecera de método de Spring
        mockMvc.perform(multipart("/carlikeafriend/features/1")
                        .file(featurePart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf())) // Solo si se tiene CSRF habilitado en los tests)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /features/{id} - Eliminar característica exitosamente")
    void deleteFeaure_Success() throws Exception {
        mockMvc.perform(delete("/carlikeafriend/features/{id}", 1L)
                        .with(csrf())) // Añadimos CSRF
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /features/images/... - Obtener archivo de imagen exitosamente")
    void getImageFile_Success() throws Exception {
        String filename = "test.jpg";
        String folder = "feature_folder";
        String imagePath = "/image/feature_folder/" + filename;
        byte[] contentBytes = "test-image-content".getBytes();

        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn(filename);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(true);

        when(fileStorageService.loadFileAsResource(filename, folder)).thenReturn(mockResource);
        when(featureService.getFeatureImageContentTypeByImagePath(imagePath)).thenReturn("image/jpeg");

        mockMvc.perform(get("/carlikeafriend/features/images/image/feature_folder/{fileName}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"test.jpg\""))
                .andExpect(content().bytes(contentBytes));
    }


}
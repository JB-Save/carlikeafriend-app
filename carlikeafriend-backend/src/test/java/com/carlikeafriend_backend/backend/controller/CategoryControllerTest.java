package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.dto.ImageDTO;
import com.carlikeafriend_backend.backend.service.ICategoryService;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula un usuario autenticado para todos los tests
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICategoryService categoryService;

    @MockitoBean
    private IFileStorageService fileStorageService;

    // --- Mocks necesarios para que el filtro de seguridad JWT no falle ---
   @MockitoBean
    private IJwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;
    // ---------------------------------------------------------------------

    private CategoryDTO categoryDTO;
    private CategoryResponseDTO categoryResponseDTO;
    private ImageDTO imageDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Premium");
        categoryDTO.setDescription("Categoría de lujo con más de diez caracteres");
        imageDTO = new ImageDTO();
        imageDTO.setId(10L);
        imageDTO.setImagePath("/image/category_folder/test.jpg");
        imageDTO.setOriginalName("test.jpg");
        imageDTO.setContentType("image/jpeg");


        categoryResponseDTO = new CategoryResponseDTO(
                1L,
                "Premium",
                "Categoría de lujo con más de diez caracteres",
                imageDTO
        );
    }

    @Test
    @DisplayName("GET /categories - Obtener todas las categorías exitosamente")
    void getAllCategories_Success() throws Exception {
        when(categoryService.findAllCategories()).thenReturn(List.of(categoryResponseDTO));

        mockMvc.perform(get("/carlikeafriend/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Premium"));
    }

    @Test
    @DisplayName("POST /categories - Crear categoría con imagen exitosamente")
    void createCategory_Success() throws Exception {
        MockMultipartFile categoryPart = new MockMultipartFile(
                "category",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(categoryDTO)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "imageFile",
                "test.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(categoryService.saveCategory(any(), any())).thenReturn(categoryResponseDTO);

        // Añadimos .with(csrf()) porque es una petición que modifica datos
        mockMvc.perform(multipart("/carlikeafriend/categories")
                        .file(categoryPart)
                        .file(imagePart)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Debería actualizar categoría e imagen")
    void updateCategory_Success() throws Exception {
        MockMultipartFile categoryPart = new MockMultipartFile("category", "", "application/json",
                objectMapper.writeValueAsBytes(categoryDTO));

        when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class), any()))
                .thenReturn(categoryResponseDTO);

        // Simulamos PUT usando multipart y la cabecera de método de Spring
        mockMvc.perform(multipart("/carlikeafriend/categories/1")
                        .file(categoryPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf())) // Solo si se tiene CSRF habilitado en los tests)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Eliminar categoría exitosamente")
    void deleteCategory_Success() throws Exception {
        mockMvc.perform(delete("/carlikeafriend/categories/{id}", 1L)
                        .with(csrf())) // Añadimos CSRF
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /categories/images/... - Obtener archivo de imagen exitosamente")
    void getImageFile_Success() throws Exception {
        String filename = "test.jpg";
        String folder = "category_folder";
        String imagePath = "/image/category_folder/" + filename;
        byte[] contentBytes = "test-image-content".getBytes();

        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn(filename);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(true);

        when(fileStorageService.loadFileAsResource(filename, folder)).thenReturn(mockResource);
        when(categoryService.getCategoryImageContentTypeByImagePath(imagePath)).thenReturn("image/jpeg");

        mockMvc.perform(get("/carlikeafriend/categories/images/image/category_folder/{fileName}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"test.jpg\""))
                .andExpect(content().bytes(contentBytes));
    }
}
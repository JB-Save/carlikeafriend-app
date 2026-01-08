package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.dto.ProductResponseDTO;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado globalmente para la clase
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProductService productService;

    @MockitoBean
    private IFileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks necesarios para que el filtro de seguridad JWT no falle ---
    @MockitoBean
    private IJwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;
    // ---------------------------------------------------------------------

    private ProductDTO productDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setName("Producto de Prueba");
        productDTO.setDescription("Esta es una descripción válida de más de diez caracteres.");
        productDTO.setPrice(150.0);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Producto de Prueba");
        productResponseDTO.setPrice(150.0);
    }

    @Test
    @DisplayName("GET /products - Debería retornar lista de productos con filtros")
    void getAllProducts_Success() throws Exception {
        List<ProductResponseDTO> productList = List.of(productResponseDTO);

        // Ajustamos el mock para devolver la lista
        when(productService.findAllFilteredProducts(any(), any(), any(), any(), any()))
                .thenReturn(productList);

        mockMvc.perform(get("/carlikeafriend/products/filter")
                        .param("minPrice", "100")
                        .param("maxPrice", "500"))
                .andExpect(status().isOk())
                // Si devuelve una lista, el JSON path empieza directamente en la raíz $
                .andExpect(jsonPath("$[0].name").value("Producto de Prueba"));
    }

    @Test
    @DisplayName("POST /products - Debería crear un producto exitosamente con imágenes")
    void createProduct_Success() throws Exception {
        // 1. Preparar el DTO y el JSON
        productDTO.setName("Producto de Prueba");
        byte[] productJson = objectMapper.writeValueAsBytes(productDTO);

        // 2. Crear las partes de la petición
        // El Controller usa @RequestPart("product") y @RequestPart("imageFiles")
        MockMultipartFile productPart = new MockMultipartFile(
                "product",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                productJson
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "imageFiles",
                "carro.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );

        // 3. Configurar el Mock
        when(productService.saveProduct(any(ProductDTO.class), any()))
                .thenReturn(productResponseDTO);

        // 4. Ejecutar y Verificar
        mockMvc.perform(multipart("/carlikeafriend/products")
                        .file(productPart)
                        .file(filePart)
                        // No forzar el Content-Type manualmente aquí si se usa .file(),
                        // MockMvc lo detectará automáticamente como multipart/form-data
                        .with(csrf())) // Solo si se tiene CSRF habilitado en los tests
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Producto de Prueba"));

        // Verificación opcional de que el servicio fue llamado
        verify(productService, times(1)).saveProduct(any(ProductDTO.class), any());
    }

    @Test
    @DisplayName("POST /products - Error 400 cuando el DTO es inválido (Nombre corto)")
    void createProduct_BadRequest_Validation() throws Exception {
        productDTO.setName("Ab"); // Invalida @Size(min=3)
        MockMultipartFile productPart = new MockMultipartFile("product", "", "application/json",
                objectMapper.writeValueAsBytes(productDTO));

        mockMvc.perform(multipart("/carlikeafriend/products")
                        .file(productPart)
                        .with(csrf())) // Solo si se tiene CSRF habilitado en los tests)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /products/{id} - Debería actualizar producto e imágenes")
    void updateProduct_Success() throws Exception {
        MockMultipartFile productPart = new MockMultipartFile("product", "", "application/json",
                objectMapper.writeValueAsBytes(productDTO));

        when(productService.updateProduct(eq(1L), any(ProductDTO.class), any(), any()))
                .thenReturn(productResponseDTO);

        // Simulamos PUT usando multipart y la cabecera de método de Spring
        mockMvc.perform(multipart("/carlikeafriend/products/1")
                        .file(productPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf())) // Solo si se tiene CSRF habilitado en los tests)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /products/images/... - Retornar archivo físico con contentType correcto")
    void getImageFile_Success() throws Exception {
        String fileName = "test.webp";
        Resource resource = new ByteArrayResource("fake-image-binary".getBytes());

        when(fileStorageService.loadFileAsResource(fileName, "product_folder")).thenReturn(resource);
        when(productService.getProductImageContentTypeByImagePath(anyString())).thenReturn("image/webp");

        mockMvc.perform(get("/carlikeafriend/products/images/image/product_folder/" + fileName))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/webp"));
    }

    @Test
    @DisplayName("DELETE /products/{id} - Retornar 204")
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/carlikeafriend/products/1")
                        .with(csrf())) // Solo si se tiene CSRF habilitado en los tests)
                .andExpect(status().isNoContent());
    }
}
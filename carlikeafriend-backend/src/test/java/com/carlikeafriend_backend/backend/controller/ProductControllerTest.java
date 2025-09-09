package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.exception.UniqueProductException;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Anotación para probar solo la capa del controlador
@DisplayName("Pruebas Unitarias para ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto principal para simular peticiones HTTP

    @MockitoBean
    private IProductService productService;

    @MockitoBean
    private IFileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private Product product;
    private MockMultipartFile productJson;
    private MockMultipartFile imageFile;
    private MockMultipartFile newImageFile;
    private Product product2;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(100.0);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setDescription("Another Description");
        product2.setPrice(200.0);

        try {
            String productJsonString = objectMapper.writeValueAsString(productDTO);
            productJson = new MockMultipartFile(
                    "product", "", "application/json", productJsonString.getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el archivo JSON simulado", e);
        }

        imageFile = new MockMultipartFile(
                "images", "test-image.jpg", "image/jpeg", "some-image-bytes".getBytes()
        );

        newImageFile = new MockMultipartFile(
                "newImages", "test-newImages.jpg", "image/jpeg", "some-image-bytes".getBytes()
        );
    }

    @Test
    @DisplayName("Crea un producto exitosamente")
    void saveProduct_Success() throws Exception {
        when(productService.saveProduct(any(ProductDTO.class), any())).thenReturn(product);

        mockMvc.perform(multipart("/carlikeafriend/products")
                        .file(productJson)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @DisplayName("Devuelve 409 CONFLICT si el producto ya existe")
    void saveProduct_Conflict() throws Exception{
        when(productService.saveProduct(any(ProductDTO.class), any())).thenThrow(new UniqueProductException(
                "Ya existe un producto con el nombre: Test Product"));

        mockMvc.perform(multipart("/carlikeafriend/products")
                .file(productJson)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isConflict()); // Expect HTTP 409 Conflict
    }

    @Test
    @DisplayName("Obtiene un producto por ID exitosamente")
    void getProductById_Success() throws Exception{
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/carlikeafriend/products/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("Devuelve 404 NOT_FOUND si el producto no existe")
    void getProductById_NotFound() throws Exception {
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carlikeafriend/products/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Actualiza un producto exitosamente")
    void updateProduct_Success() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(200.0);

        when(productService.updateProduct(any(Long.class), any(ProductDTO.class), any(List.class), any(List.class)))
                .thenReturn(updatedProduct);

        String productDTOUpdateJson = objectMapper.writeValueAsString(productDTO);
        MockMultipartFile productJsonUpdated = new MockMultipartFile(
             "product", "", "application/json", productDTOUpdateJson.getBytes()
        );

        MockMultipartFile imagesToDeleteJson = new MockMultipartFile(
                "imagesToDelete", "", "application/json", "[1, 2]".getBytes()
        );

        mockMvc.perform(multipart("/carlikeafriend/products/{id}", 1L)
                .file(productJsonUpdated)
                .file(imagesToDeleteJson)
                .file(newImageFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @DisplayName("Devuelve 404 NOT_FOUND al actualizar un producto que no existe")
    void updateProduct_NotFound() throws Exception {
        when(productService.updateProduct(any(Long.class), any(ProductDTO.class), any(), any()))
                .thenThrow(new RuntimeException("Producto no encontrado con ID: 99"));

        String productDTOUpdatedJson = objectMapper.writeValueAsString(productDTO);
        MockMultipartFile productJsonUpdated = new MockMultipartFile(
                "product", "", "application/json", productDTOUpdatedJson.getBytes()
        );

        mockMvc.perform(multipart("/carlikeafriend/products/{id}", 99L)
                        .file(productJsonUpdated)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Actualización parcial (PATCH) exitosamente")
    void patchProduct_Success() throws Exception {
        // Objeto DTO parcial con solo el campo que queremos actualizar
        ProductDTO partialProductDto = new ProductDTO();
        partialProductDto.setPrice(150.50);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Test Product");
        updatedProduct.setDescription("Test Description");
        updatedProduct.setPrice(150.50);

        when(productService.patchProduct(any(Long.class), any(ProductDTO.class), any(), any()))
                .thenReturn(updatedProduct);

        String partialJson = objectMapper.writeValueAsString(partialProductDto);
        MockMultipartFile productPart = new MockMultipartFile("product", "", "application/json", partialJson.getBytes());

        mockMvc.perform(multipart("/carlikeafriend/products/{id}", 1L)
                        .file(productPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(150.50))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("Devuelve 404 NOT_FOUND al intentar aplicar un PATCH a un producto que no existe")
    void patchProduct_NotFound() throws Exception {
        when(productService.patchProduct(any(Long.class), any(ProductDTO.class), any(), any()))
                .thenThrow(new RuntimeException("Producto no encontrado con ID: 99"));

        String partialJson = objectMapper.writeValueAsString(new ProductDTO());
        MockMultipartFile productPart = new MockMultipartFile("product", "", "application/json", partialJson.getBytes());

        mockMvc.perform(multipart("/carlikeafriend/products/{id}", 99L)
                        .file(productPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Elimina un producto exitosamente")
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/carlikeafriend/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Devuelve 404 NOT_FOUND al eliminar un producto que no existe")
    void deleteProduct_NotFound() throws Exception {
        doThrow(new RuntimeException("Producto no encontrado con ID: 99"))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/carlikeafriend/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Devuelve todos los productos exitosamente")
    void getAllProducts_Success() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(product, product2));

        mockMvc.perform(get("/carlikeafriend/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[1].name").value("Another Product"));
    }

    @Test
    @DisplayName("Devuelve una lista vacía si no hay productos")
    void getAllProducts_EmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/carlikeafriend/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Descarga la imagen exitosamente")
    void downloadImage_Success() throws Exception {
        String filename = "test-image.jpg";
        String imagePath = "image/" + filename;
        byte[] contentBytes = "test-image-content".getBytes();

        // Creamos un mock de Resource para simular el archivo
        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn(filename);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));

        when(fileStorageService.loadFileAsResource(filename)).thenReturn(mockResource);

        mockMvc.perform(get("/carlikeafriend/products/images/{filename}", imagePath))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\""))
                .andExpect(content().bytes(contentBytes));
    }

    @Test
    @DisplayName("Devuelve 404 NOT_FOUND si la imagen no existe")
    void downloadImage_NotFound() throws Exception {
        String filename = "nonexistent-image.jpg";
        when(fileStorageService.loadFileAsResource(filename)).thenThrow(new RuntimeException("Producto no encontrado"));

        mockMvc.perform(get("/carlikeafriend/products/images/image/{filename}", filename))
                .andExpect(status().isNotFound());
    }
}
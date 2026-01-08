package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.dto.ProductResponseDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.entity.ProductImage;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.service.impl.ProductService;
import com.carlikeafriend_backend.backend.util.FileValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private IProductRepository productRepository;
    @Mock private IFileStorageService fileStorageService;
    @Mock private FileValidationUtils fileValidationUtils;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Producto Original");
        product.setImages(new ArrayList<>());

        productDTO = new ProductDTO();
        productDTO.setName("Producto Actualizado");
        productDTO.setDescription("Descripción válida del producto");
        productDTO.setPrice(100.0);
    }

    @Test
    @DisplayName("Crear Producto - Éxito al crear con imágenes válidas")
    void createProduct_Success() throws IOException {
        MultipartFile file = new MockMultipartFile("newImage", "newImage.jpg", "image/jpeg", "some-image-bytes".getBytes());
        List<MultipartFile> files = List.of(file);

        when(productRepository.existsByName(anyString())).thenReturn(false);
        when(fileStorageService.storeFile(eq(file), anyString())).thenReturn("/image/product_folder/newImage.jpg");
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponseDTO result = productService.saveProduct(productDTO, files);

        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        verify(fileValidationUtils).validateAtLeastOneImage(files);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Crear Producto - Error si el nombre del producto ya existe")
    void createProduct_ThrowsUniqueName() {
        when(productRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(UniqueNameException.class, () -> productService.saveProduct(productDTO, null));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar Producto - Debería eliminar imágenes marcadas y agregar nuevas")
    void updateProduct_FullSync_Success() throws IOException {
        // Imagen que se va a borrar
        ProductImage oldImg = new ProductImage();
        oldImg.setId(50L);
        oldImg.setImagePath("/image/product_folder/delete.jpg");
        product.getImages().add(oldImg);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);

        MultipartFile newFile = new MockMultipartFile("newImage", "newImage.jpg", "image/jpeg", "some-image-bytes".getBytes());
        when(fileStorageService.storeFile(eq(newFile), anyString())).thenReturn("/image/product_folder/new.jpg");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateProduct(1L, productDTO, List.of(newFile), List.of(50L));

        verify(eventPublisher).publishEvent(any(ImageDeletedEvent.class)); // Verificamos evento de borrado físico
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Eliminar Producto - Debería lanzar excepción si no existe")
    void deleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    @DisplayName("Eliminar Producto - Éxito y publicación de eventos de borrado de archivos")
    void deleteProduct_Success() throws IOException {
        ProductImage img = new ProductImage();
        img.setImagePath("/image/product_folder/img.jpg");
        product.getImages().add(img);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
        verify(eventPublisher).publishEvent(any(ImageDeletedEvent.class));
    }
}
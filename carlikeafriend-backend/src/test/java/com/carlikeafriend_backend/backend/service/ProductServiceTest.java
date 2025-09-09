package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.entity.Image;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.UniqueProductException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.service.impl.FileStorageService;
import com.carlikeafriend_backend.backend.service.impl.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para ProductService")
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProductService productService;

    private ProductDTO productDTO;
    private Product product;
    private List<MultipartFile> images;
    private Image image1;
    private Image image2;

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

        image1 = new Image();
        image1.setId(101L);
        image1.setImagePath("/image/image1.jpg");

        image2 = new Image();
        image2.setId(102L);
        image2.setImagePath("/image/image2.jpg");

        product.getImages().add(image1);
        product.getImages().add(image2);

        // Se configura el mock para una única imagen
        MultipartFile mockImage = mock(MultipartFile.class);
        images = List.of(mockImage);
    }

    @Test
    @DisplayName("Guardar un producto exitosamente")
    void saveProduct_Success() {

        when(images.get(0).getOriginalFilename()).thenReturn("test-image.jpg");
        when(images.get(0).getContentType()).thenReturn("image/jpeg");

        when(productRepository.findByName(productDTO.getName())).thenReturn(Optional.empty());
        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("/image/test-image.jpg");

        // Creamos un captor para verificar el objeto Product que se pasa al método save
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // Devolvemos el mismo objeto capturado para que la prueba sea precisa
        when(productRepository.save(productCaptor.capture())).thenAnswer(invocation -> {
                    Product savedProduct = invocation.getArgument(0);
                    savedProduct.setId(1L);
                    savedProduct.getImages().get(0).setId(1L);
                    return savedProduct;
                }
        );

        Product result = productService.saveProduct(productDTO, images);

        assertNotNull(result);


        // Capturamos el objeto y verificamos su estado
        Product capturedProduct = productCaptor.getValue();

        assertEquals(1, capturedProduct.getImages().size());
        assertEquals(productDTO.getName(), capturedProduct.getName());
        verify(productRepository, times(1)).findByName(productDTO.getName());
        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class));
        verify(productRepository, times(1)).save(productCaptor.capture());

    }

    @Test
    @DisplayName("Lanza una UniqueProductException si el nombre ya existe")
    void saveProduct_UniqueProductException() {
        when(productRepository.findByName(productDTO.getName())).thenReturn(Optional.of(product));
        assertThrows(UniqueProductException.class, () -> productService.saveProduct(productDTO, images));
        verify(productRepository, never()).save(any(Product.class));
        verify(fileStorageService, never()).storeFile(any(MultipartFile.class));
    }

    @Test
    @DisplayName("Lanza una ImageLimitExceededException si el límite de imágenes es excedido")
    void saveProduct_ImageLimitExceededException() {
        List<MultipartFile> excessiveImages = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            excessiveImages.add(mock(MultipartFile.class));
        }
        assertThrows(ImageLimitExceededException.class, () -> productService.saveProduct(productDTO, excessiveImages));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Obtiene un producto por ID exitosamente")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Optional<Product> foundProduct = productService.getProductById(1L);
        assertTrue(foundProduct.isPresent());
        assertEquals(product.getName(), foundProduct.get().getName());
    }

    @Test
    @DisplayName("Devuelve un Optional vacío si el producto no existe")
    void getProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Product> foundProduct = productService.getProductById(999L);
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Elimina un producto y sus archivos exitosamente")
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);
        verify(fileStorageService, times(2)).deleteFile(anyString());
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("No Elimina el producto si no existe")
    void deleteProduct_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, never()).delete(any(Product.class));
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    @DisplayName("Lista todos los productos exitosamente")
    void getAllProducts_Success() {
        List<Product> mockProducts = List.of(product, new Product());
        when(productRepository.findAll()).thenReturn(mockProducts);
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualiza un producto exitosamente")
    void updateProduct_Success() {
        Long productId = 1L;
        ProductDTO updatedDto = new ProductDTO();
        updatedDto.setName("Updated Product Name");
        updatedDto.setDescription("Updated Description");
        updatedDto.setPrice(150.0);

        List<MultipartFile> newImages = List.of(mock(MultipartFile.class));
        when(newImages.get(0).getOriginalFilename()).thenReturn("new-image.jpg");

        List<Long> imagesToDeleteIds = List.of(image1.getId());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("/image/new-image.jpg");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.getImages().get(1).setId(103L);
            return savedProduct;
        });

        // Ejecutar el método a probar
        Product result = productService.updateProduct(productId, updatedDto, newImages, imagesToDeleteIds);

        // Verificaciones
        assertNotNull(result);
        assertEquals(updatedDto.getName(), result.getName());
        assertEquals(updatedDto.getDescription(), result.getDescription());
        assertEquals(updatedDto.getPrice(), result.getPrice());

        // Verificar que se eliminó la imagen correcta y se añadió la nueva
        verify(fileStorageService, times(1)).deleteFile(image1.getImagePath());
        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class));
        verify(productRepository, times(1)).save(any(Product.class));

        // Verificar el estado de la lista de imágenes del producto
        assertEquals(2, result.getImages().size());
        assertFalse(result.getImages().stream().anyMatch(img -> img.getId().equals(image1.getId())));
        assertTrue(result.getImages().stream().anyMatch(img -> img.getImagePath().equals("/image/new-image.jpg")));
    }

    @Test
    @DisplayName("Lanza una excepción si el producto a actualizar no existe")
    void updateProduct_ProductNotFound() {
        Long nonExistentId = 999L;
        ProductDTO updatedDto = new ProductDTO();

        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(nonExistentId, updatedDto, null, null));

        verify(productRepository, never()).save(any(Product.class));
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(fileStorageService, never()).storeFile(any(MultipartFile.class));
    }
}



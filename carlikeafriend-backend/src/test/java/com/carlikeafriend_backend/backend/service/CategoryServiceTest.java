package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.entity.Category;
import com.carlikeafriend_backend.backend.entity.CategoryImage;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.ICategoryRepository;
import com.carlikeafriend_backend.backend.service.impl.CategoryService;
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
public class CategoryServiceTest {


    @Mock
    private ICategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private FileValidationUtils fileValidationUtils;

    @InjectMocks
    private CategoryService categoryService;

    private Category existingCategory;
    private CategoryDTO updateDTO;

    @BeforeEach
    void setUp() {
        existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Antigua");
        existingCategory.setDescription("Descripción antigua");

        CategoryImage img = new CategoryImage();
        img.setImagePath("/image/category_folder/old.jpg");
        existingCategory.setCategoryImage(img);

        updateDTO = new CategoryDTO();
        updateDTO.setName("Nueva");
        updateDTO.setDescription("Nueva descripción de más de diez caracteres");
    }

    @Test
    @DisplayName("Actualizar categoría: Solo texto, mantiene imagen anterior")
    void updateCategory_OnlyText_KeepOldImage() throws IOException {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);

        CategoryResponseDTO result = categoryService.updateCategory(1L, updateDTO, null);

        assertEquals("Nueva", result.getName());
        assertEquals("/image/category_folder/old.jpg", result.getCategoryImage().getImagePath());
        verify(fileStorageService, never()).storeFile(any(), any());
    }

    @Test
    @DisplayName("Lanza UniqueNameException si el nombre ya existe al actualizar")
    void updateCategory_DuplicateName_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(true);

        assertThrows(UniqueNameException.class, () ->
                categoryService.updateCategory(1L, updateDTO, any())
        );
    }

    @Test
    @DisplayName("Eliminar categoría: Verifica publicación de evento para borrado físico")
    void deleteCategory_PublishesEvent() throws IOException {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(existingCategory);
        verify(eventPublisher, times(1)).publishEvent(new ImageDeletedEvent(any()));
    }

    @Test
    @DisplayName("Actualizar categoría: Con nueva imagen, reemplaza la anterior")
    void updateCategory_WithNewImage_ReplacesOldOne() throws IOException {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        MultipartFile mockFile = new MockMultipartFile("newImage", "newImage.jpg", "image/jpeg", "some-image-bytes".getBytes());

        // El servicio llama a validateImageFile, como es un mock de void no hace nada (comportamiento deseado)
        doNothing().when(fileValidationUtils).validateImageFile(mockFile);

        when(fileStorageService.storeFile(eq(mockFile), anyString())).thenReturn("/image/category_folder/newImage.jpg");
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);


        CategoryResponseDTO result = categoryService.updateCategory(1L, updateDTO, mockFile);

        assertEquals("/image/category_folder/newImage.jpg", result.getCategoryImage().getImagePath());
        verify(eventPublisher).publishEvent(new ImageDeletedEvent(any()));
        verify(fileStorageService).storeFile(mockFile, "category_folder");
        verify(fileValidationUtils).validateImageFile(mockFile); // Verifica que se validó el archivo
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException al actualizar categoría inexistente")
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.updateCategory(99L, updateDTO, null)
        );
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Uso de ArgumentCaptor para verificar campos persistidos")
    void updateCategory_VerifyPersistenceWithCaptor() throws IOException {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        categoryService.updateCategory(1L, updateDTO, null);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());

        Category saved = captor.getValue();
        assertEquals("Nueva", saved.getName());
        assertEquals(updateDTO.getDescription(), saved.getDescription());
    }
}
package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.entity.Category;
import com.carlikeafriend_backend.backend.entity.CategoryImage;
import com.carlikeafriend_backend.backend.event.CategoryUpdatedEvent;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
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
        existingCategory.setBaseDailyRate(100000.0);
        existingCategory.setPriority(50);

        CategoryImage img = new CategoryImage();
        img.setImagePath("/image/category_folder/old.jpg");
        existingCategory.setCategoryImage(img);

        updateDTO = new CategoryDTO();
        updateDTO.setName("Nueva");
        updateDTO.setDescription("Nueva descripción de más de diez caracteres");
        updateDTO.setBaseDailyRate(110000.0);
        updateDTO.setPriority(100);
    }

    @Test
    @DisplayName("Actualizar categoría: Lanza evento si cambian valores financieros")
    void updateCategory_FinanceFieldsChanged_PublishesEvent() throws IOException {
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);

        categoryService.updateCategory(1L, updateDTO, null);

        verify(eventPublisher).publishEvent(any(CategoryUpdatedEvent.class));
    }

    @Test
    @DisplayName("Crear categoría: Lanza excepción y detiene persistencia si el archivo es inválido")
    void createCategory_InvalidFile_StopsExecution() throws Exception {
        MultipartFile mockFile = new MockMultipartFile("invalid", "test.txt", "text/plain", "content".getBytes());
        doThrow(new InvalidFileExtensionException("No permitido"))
                .when(fileValidationUtils).validateImageFile(mockFile);

        assertThrows(InvalidFileExtensionException.class, () ->
                categoryService.saveCategory(updateDTO, mockFile)
        );
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar categoría: Solo texto, mantiene imagen anterior")
    void updateCategory_OnlyText_KeepOldImage() throws IOException {
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);

        CategoryResponseDTO result = categoryService.updateCategory(1L, updateDTO, null);

        assertEquals("Nueva", result.getName());
        assertEquals("/image/category_folder/old.jpg", result.getCategoryImage().getImagePath());
        verify(fileStorageService, never()).storeFile(any(), any());
    }

    @Test
    @DisplayName("Lanza DuplicateResourceException si el nombre ya existe al actualizar")
    void updateCategory_DuplicateName_ThrowsException() {
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndIdNotAndDeletedFalse(anyString(), anyLong())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                categoryService.updateCategory(1L, updateDTO, any())
        );
    }

    @Test
    @DisplayName("Eliminar categoría: Verifica el borrado lógico y mutación del nombre")
    void deleteCategory_LogicalDelete() throws IOException {
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCategory));

        categoryService.deleteCategory(1L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());

        Category saved = captor.getValue();
        assertTrue(saved.isDeleted());
        assertTrue(saved.getName().contains("_DELETED_"));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException al actualizar categoría inexistente")
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.updateCategory(99L, updateDTO, null)
        );
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Uso de ArgumentCaptor para verificar campos persistidos")
    void updateCategory_VerifyPersistenceWithCaptor() throws IOException {
        when(categoryRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        categoryService.updateCategory(1L, updateDTO, null);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());

        Category saved = captor.getValue();
        assertEquals("Nueva", saved.getName());
        assertEquals(updateDTO.getDescription(), saved.getDescription());
        assertEquals(updateDTO.getBaseDailyRate(), saved.getBaseDailyRate());
        assertEquals(updateDTO.getPriority(), saved.getPriority());
    }
}
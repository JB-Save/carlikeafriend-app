package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.ImageDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.entity.Category;
import com.carlikeafriend_backend.backend.entity.CategoryImage;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.ICategoryImageRepository;
import com.carlikeafriend_backend.backend.repository.ICategoryRepository;
import com.carlikeafriend_backend.backend.service.ICategoryService;
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
public class CategoryService implements ICategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final ICategoryRepository categoryRepository;
    private final IFileStorageService fileStorageService;
    private final ICategoryImageRepository categoryImageRepository;
    private final FileValidationUtils fileValidationUtils; // Inyección de utilidad
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CategoryService(ICategoryRepository categoryRepository,
                           IFileStorageService fileStorageService,
                           ICategoryImageRepository categoryImageRepository,
                           FileValidationUtils fileValidationUtils,
                           ApplicationEventPublisher eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.categoryImageRepository = categoryImageRepository;
        this.fileValidationUtils = fileValidationUtils;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public CategoryResponseDTO saveCategory(CategoryDTO categoryDTO, MultipartFile imageFile) throws
            UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException {

        logger.info("Intentando guardar nueva categoría: {}", categoryDTO.getName());

        // Validación de duplicados por nombre
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            logger.warn("El nombre de la categoría ya existe: {}", categoryDTO.getName());
            throw new UniqueNameException("El nombre de la categoría ya existe.");
        }

        // Validación de archivos usando la utilidad
        fileValidationUtils.validateAtLeastOneImage(imageFile);
        fileValidationUtils.validateImageFile(imageFile);

        // Mapear DTO a Entidad
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        // Procesar Imagen
        if (imageFile.getSize() > 0 && !imageFile.isEmpty()) {
            String imagePath = fileStorageService.storeFile(imageFile, "category_folder");
            CategoryImage categoryImage = new CategoryImage();
            categoryImage.setImagePath(imagePath);
            categoryImage.setOriginalName(imageFile.getOriginalFilename());
            categoryImage.setContentType(imageFile.getContentType());
            category.setCategoryImage(categoryImage);
        }

        Category savedCategory = categoryRepository.save(category);
        logger.info("Categoría guardada exitosamente con ID: {}", savedCategory.getId());
        return convertToDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAllCategories() {
        logger.info("Buscando todas las categorías.");
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponseDTO> findCategoryById(Long id) {
        logger.info("Buscando categoría con ID: {}", id);
        return categoryRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryDTO categoryDTO, MultipartFile newImageFile) throws
            UniqueNameException, InvalidFileExtensionException, IOException {

        logger.info("Intentando actualizar categoría con ID: {}", id);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Validar que el nombre de la categoría sea único, excluyendo la categoría actual
        if (categoryDTO.getName() != null && !categoryDTO.getName().equals(existingCategory.getName()) && categoryRepository.existsByNameAndIdNot(categoryDTO.getName(), id)) {
            throw new UniqueNameException("El nombre de la categoría ya existe.");
        }


        // Actualizar datos básicos de la categoría
        Optional.ofNullable(categoryDTO.getName()).ifPresent(existingCategory::setName);
        Optional.ofNullable(categoryDTO.getDescription()).ifPresent(existingCategory::setDescription);

        // Validación de nueva imagen
        if (newImageFile != null) {
            fileValidationUtils.validateImageFile(newImageFile);

            // Eliminar la imagen antigua si existe
            if (existingCategory.getCategoryImage() != null) {
                // Solo agendamos el borrado para DESPUÉS de que se guarde todo bien
                eventPublisher.publishEvent(new ImageDeletedEvent(existingCategory.getCategoryImage().getImagePath()));
            }

            // Guardar la nueva imagen
            String imagePath = fileStorageService.storeFile(newImageFile, "category_folder");
            CategoryImage categoryImage = new CategoryImage();
            categoryImage.setImagePath(imagePath);
            categoryImage.setOriginalName(newImageFile.getOriginalFilename());
            categoryImage.setContentType(newImageFile.getContentType());
            existingCategory.setCategoryImage(categoryImage);
        }


        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDto(updatedCategory);

    }

    @Override
    @Transactional
    public void deleteCategory(Long id) throws IOException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Guardar la ruta temporalmente
        String pathToDelete = (category.getCategoryImage() != null) ? category.getCategoryImage().getImagePath() : null;

        // Intentar borrar de la DB.
        // Si hay una FK Constraint (DataIntegrityViolationException), explota aquí
        // y NUNCA llega a la línea del evento.
        categoryRepository.delete(category);

        // Si llegamos aquí, la DB aceptó la orden (aunque aún no ha hecho commit).
        // Publicamos el evento. Spring lo guardará y solo lo ejecutará si el commit final funciona.
        if (pathToDelete != null) {
            eventPublisher.publishEvent(new ImageDeletedEvent(pathToDelete));
        }
        logger.warn("Categoría marcada para eliminación con ID: {}", id);
    }

    @Override
    public String getCategoryImageContentTypeByImagePath(String imagePath) {
        return categoryImageRepository.findContentTypeByImagePath(imagePath)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta de imagen no encontrada"));
    }


    private CategoryResponseDTO convertToDto(Category category) {
        ImageDTO categoryImageDto = null;
        if (category.getCategoryImage() != null) {
            categoryImageDto = new ImageDTO();
            categoryImageDto.setId(category.getCategoryImage().getId());
            categoryImageDto.setImagePath(category.getCategoryImage().getImagePath());
            categoryImageDto.setOriginalName(category.getCategoryImage().getOriginalName());
            categoryImageDto.setContentType(category.getCategoryImage().getContentType());
        }
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                categoryImageDto
        );
    }
}

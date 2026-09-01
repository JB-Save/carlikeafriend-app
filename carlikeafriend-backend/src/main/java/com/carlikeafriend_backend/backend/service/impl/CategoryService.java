package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.ImageDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.entity.Category;
import com.carlikeafriend_backend.backend.entity.CategoryImage;
import com.carlikeafriend_backend.backend.event.CategoryUpdatedEvent;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.ICategoryImageRepository;
import com.carlikeafriend_backend.backend.repository.ICategoryRepository;
import com.carlikeafriend_backend.backend.service.ICategoryService;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.util.FileValidationUtils;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
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
            DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException {

        String categoryName = StringUtils.capitalize(categoryDTO.getName());

        logger.info("Intentando guardar nueva categoría: {}", categoryName);

        // Validación de duplicados por nombre
        if (categoryRepository.existsByNameAndDeletedFalse(categoryName)) {
            logger.warn("Ya existe una categoría activa con el nombre: {}", categoryName);
            throw new DuplicateResourceException("Ya existe una categoría activa con el nombre: " + categoryName);
        }

        // Validación de archivos usando la utilidad
        fileValidationUtils.validateAtLeastOneImage(imageFile);
        fileValidationUtils.validateImageFile(imageFile);

        // Mapear DTO a Entidad
        Category category = new Category();
        category.setName(categoryName);
        category.setDescription(categoryDTO.getDescription());
        category.setBaseDailyRate(categoryDTO.getBaseDailyRate());
        category.setPriority(categoryDTO.getPriority());
        category.setBaseDepositAmount(categoryDTO.getBaseDepositAmount());

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
        return mapToCategoryDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        logger.info("Buscando todas las categorías.");
        return categoryRepository.findAllByDeletedFalse().stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        logger.info("Buscando categoría con ID: {}", id);
        return categoryRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToCategoryDto);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryDTO categoryDTO, MultipartFile newImageFile) throws
            DuplicateResourceException, InvalidFileExtensionException, IOException {

        logger.info("Intentando actualizar categoría con ID: {}", id);

        String categoryName = StringUtils.capitalize(categoryDTO.getName());

        Category existingCategory = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Categoría no encontrada con ID: " + id));

        // Guardamos los valores existentes del precio, la prioridad y el depósito
        Double oldBaseDailyRate = existingCategory.getBaseDailyRate();
        Integer oldPriority = existingCategory.getPriority();
        Double oldBaseDepositAmount = existingCategory.getBaseDepositAmount();


        // Validar que el nombre de la categoría activa sea único, excluyendo la categoría actual
        if (categoryName != null && !categoryName.equals(StringUtils.capitalize(existingCategory.getName()))) {
            if (categoryRepository.existsByNameAndIdNotAndDeletedFalse(categoryName, id)) {
                throw new DuplicateResourceException("El nombre " + categoryName + " ya está en uso por otra categoría activa.");
            }
            existingCategory.setName(categoryName);
        }

        // Actualizar datos básicos de la categoría
        Optional.ofNullable(categoryDTO.getDescription()).ifPresent(existingCategory::setDescription);
        Optional.ofNullable(categoryDTO.getBaseDailyRate()).ifPresent(existingCategory::setBaseDailyRate);
        Optional.ofNullable(categoryDTO.getPriority()).ifPresent(existingCategory::setPriority);
        Optional.ofNullable(categoryDTO.getBaseDepositAmount()).ifPresent(existingCategory::setBaseDepositAmount);

        // Validación de nueva imagen
        if (newImageFile != null && !newImageFile.isEmpty()) {
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

        // Si cambió el precio, la prioridad o el depósito, avisamos al sistema
        if (hasTheFinanceCategoryChanged(oldPriority, oldBaseDailyRate, oldBaseDepositAmount, updatedCategory)) {
            eventPublisher.publishEvent(new CategoryUpdatedEvent(updatedCategory.getId()));
        }

        return mapToCategoryDto(updatedCategory);

    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndDeletedFalse((id))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (category.hasActiveProducts()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen productos activos asociados a esta categoría.");
        }

        // Liberar el nombre único para futuras creaciones
        String timestamp = String.valueOf(System.currentTimeMillis());
        category.setName(category.getName() + "_DELETED_" + timestamp);

        // Borrado lógico
        category.setDeleted(true);

        categoryRepository.save(category);
        logger.info("Categoría con ID: {} borrado lógicamente.", id);
/*
        // --- PROTECCIÓN DE INTEGRIDAD (RESTRICTIVO) ---
        validateEmpty(category.getProducts(), "productos", id);

        // Guardar la ruta temporalmente
        String pathToDelete = (category.getCategoryImage() != null) ? category.getCategoryImage().getImagePath() : null;

        // Intentar borrar de la DB.
        categoryRepository.delete(category);

        // Si llegamos aquí, la DB aceptó la orden (aunque aún no ha hecho commit).
        // Publicamos el evento. Spring lo guardará y solo lo ejecutará si el commit final funciona.
        if (pathToDelete != null) {
            eventPublisher.publishEvent(new ImageDeletedEvent(pathToDelete));
        }
        logger.warn("Categoría marcada para eliminación con ID: {}", id);

 */
    }

    @Override
    public String getCategoryImageContentTypeByImagePath(String imagePath) {
        return categoryImageRepository.findContentTypeByImagePath(imagePath)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta de imagen no encontrada"));
    }

    private void validateEmpty(Collection<?> collection, String entityName, Long id) {
        if (!collection.isEmpty()) {
            logger.error("No se puede eliminar la categoría ID: {}. Tiene {} {} asociado/a(s).", id, collection.size(), entityName);
            throw new DataIntegrityViolationException("No se puede eliminar la categoría porque tiene " + entityName + " asociado/a(s).");
        }
    }

    private boolean hasTheFinanceCategoryChanged(Integer oldPriority, Double oldBaseDailyRate, Double oldBaseDepositAmount, Category updatedCategory) {
        return !oldPriority.equals(updatedCategory.getPriority()) ||
                !oldBaseDailyRate.equals(updatedCategory.getBaseDailyRate()) ||
                !oldBaseDepositAmount.equals(updatedCategory.getBaseDepositAmount());
    }


        private CategoryResponseDTO mapToCategoryDto(Category category){
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
                    category.getBaseDailyRate(),
                    category.getPriority(),
                    category.getBaseDepositAmount(),
                    categoryImageDto
            );
        }
    }

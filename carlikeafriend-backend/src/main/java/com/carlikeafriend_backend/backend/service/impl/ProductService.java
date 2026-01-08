package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.Category;
import com.carlikeafriend_backend.backend.entity.Feature;
import com.carlikeafriend_backend.backend.entity.ProductImage;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.ICategoryRepository;
import com.carlikeafriend_backend.backend.repository.IFeatureRepository;
import com.carlikeafriend_backend.backend.repository.IProductImageRepository;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IProductService;
import com.carlikeafriend_backend.backend.specification.ProductSpecifications;
import com.carlikeafriend_backend.backend.util.FileValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private static final int MAX_IMAGES_PER_PRODUCT = 5; // Definir el límite de imágenes

    private final IProductRepository productRepository;
    private final IFileStorageService fileStorageService;
    private final IProductImageRepository productImageRepository;
    private final ICategoryRepository categoryRepository;
    private final IFeatureRepository featureRepository;
    private final FileValidationUtils fileValidationUtils; // Inyección de utilidad
    private final ApplicationEventPublisher eventPublisher;


    @Autowired
    public ProductService(IProductRepository productRepository,
                          IFileStorageService fileStorageService,
                          IProductImageRepository productImageRepository,
                          ICategoryRepository categoryRepository,
                          IFeatureRepository featureRepository,
                          FileValidationUtils fileValidationUtils,
                          ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.featureRepository = featureRepository;
        this.fileValidationUtils = fileValidationUtils;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public ProductResponseDTO saveProduct(ProductDTO productDTO, List<MultipartFile> imageFiles) throws
            UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException {

        logger.info("Intentando guardar nuevo producto: {}", productDTO.getName());

        // Validación de duplicados por nombre
        if (productRepository.existsByName(productDTO.getName())) {
            logger.warn("El nombre del producto ya existe: {}", productDTO.getName());
            throw new UniqueNameException("El nombre del producto ya existe.");
        }

        // Validación de archivos usando la utilidad
        fileValidationUtils.validateAtLeastOneImage(imageFiles);
        fileValidationUtils.validateImageFiles(imageFiles);

        // Contar imágenes válidas reales
        long validImagesCount = imageFiles.stream()
                .filter(f -> f != null && !f.isEmpty() && f.getSize() > 0)
                .count();

        fileValidationUtils.validateImageCount((int) validImagesCount, MAX_IMAGES_PER_PRODUCT);

        // Mapear DTO a Entidad
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        // Asociar Categorías
        Set<Category> managedCategories = resolveCategories(productDTO.getCategories());
        if (!managedCategories.isEmpty()) {
            managedCategories.forEach(cat -> cat.addProduct(product));
        }

        // Asociar Características
        Set<Feature> managedFeatures = resolveFeatures(productDTO.getFeatures());
        if (!managedFeatures.isEmpty()) {
            managedFeatures.forEach(feat -> feat.addProduct(product));
        }

        // Procesar Imágenes
        if (!imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile.getSize() > 0 && !imageFile.isEmpty()) {
                    String imagePath = fileStorageService.storeFile(imageFile, "product_folder");
                    ProductImage productImage = new ProductImage();
                    productImage.setImagePath(imagePath);
                    productImage.setOriginalName(imageFile.getOriginalFilename());
                    productImage.setContentType(imageFile.getContentType());
                    product.addImage(productImage);
                }
            }
        }


        Product savedProduct = productRepository.save(product);
        logger.info("Producto guardado con ID: {}", savedProduct.getId());
        return convertToDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllProducts() {
        logger.info("Buscando todos los productos.");
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> findProductById(Long id) {
        logger.info("Buscando producto con ID: {}", id);
        return productRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllFilteredProducts(List<Long> categoryIds,
                                                            List<Long> featureIds,
                                                            Double minPrice,
                                                            Double maxPrice,
                                                            String sortBy) {

        logger.info("Filtrando productos...");

        // 1. Creamos una lista para almacenar las especificaciones dinámicas
        List<Specification<Product>> specs = new ArrayList<>();

        // 2. Agregamos condiciones a la lista solo si los datos existen
        if (minPrice != null && maxPrice != null) {
            specs.add(ProductSpecifications.priceBetween(minPrice, maxPrice));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            specs.add(ProductSpecifications.inAllCategories(categoryIds));
        }

        if (featureIds != null && !featureIds.isEmpty()) {
            specs.add(ProductSpecifications.inAllFeatures(featureIds));
        }

        // 3. Reducimos la lista a una sola Specification usando 'and'
        // Si la lista está vacía, usamos Specification.allOf() (que equivale a "traer todo")
        Specification<Product> finalSpec = specs.stream()
                .reduce(Specification::and)
                .orElse(Specification.allOf());

        // 4. Lógica de Ordenamiento. Ahora pasamos el string compuesto "campo_direccion"
        Sort sort = ProductSpecifications.createSort(sortBy);


        // 5. Consulta al Repositorio con Spec + Sort
        return productRepository.findAll(finalSpec, sort).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> newImageFiles, List<Long> imagesToDeleteIds) throws UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException {
        logger.info("Intentando actualizar producto con ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));


        // Validar que el nombre del producto sea único, excluyendo el producto actual
        if (productDTO.getName() != null && !productDTO.getName().equals(existingProduct.getName()) && productRepository.existsByNameAndIdNot(productDTO.getName(), id)) {
            throw new UniqueNameException("El nombre del producto ya existe.");
        }

        // Actualizar datos básicos del producto
        Optional.ofNullable(productDTO.getName()).ifPresent(existingProduct::setName);
        Optional.ofNullable(productDTO.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.of(productDTO.getPrice()).ifPresent(existingProduct::setPrice);

        // Actualizar relaciones (Lógica simplificada usando métodos auxiliares)
        updateCategories(existingProduct, productDTO.getCategories());
        updateFeatures(existingProduct, productDTO.getFeatures());

        // Lógica de Imágenes
        int currentCount = existingProduct.getImages().size();
        int deleteCount = (imagesToDeleteIds != null) ? imagesToDeleteIds.size() : 0;

        // Validación de nuevas imágenes
        if (newImageFiles != null) {
            fileValidationUtils.validateImageFiles(newImageFiles);
        }
        int newCount = (newImageFiles != null) ? (int) newImageFiles.stream().filter(f -> !f.isEmpty()).count() : 0;
        int finalCount = currentCount - deleteCount + newCount;

        // Validar que no quede sin imágenes
        if (finalCount == 0) {
            throw new ImageLimitExceededException("El producto debe tener al menos una imagen.");
        }
        // Validar máximo
        fileValidationUtils.validateImageCount(finalCount, MAX_IMAGES_PER_PRODUCT);

        // Eliminar imágenes
        if (imagesToDeleteIds != null && !imagesToDeleteIds.isEmpty()) {
            List<ProductImage> imagesToRemove = existingProduct.getImages().stream()
                    .filter(img -> imagesToDeleteIds.contains(img.getId()))
                    .collect(Collectors.toList());

            for (ProductImage image : imagesToRemove) {
                // Disparamos el evento. El Listener borrará el archivo SOLO si el commit es exitoso.
                eventPublisher.publishEvent(new ImageDeletedEvent(image.getImagePath()));

                existingProduct.removeImage(image);
            }
        }

        // Agregar nuevas imágenes
        if (newImageFiles != null) {
            for (MultipartFile file : newImageFiles) {
                if (!file.isEmpty()) {
                    String path = fileStorageService.storeFile(file, "product_folder");
                    ProductImage img = new ProductImage();
                    img.setImagePath(path);
                    img.setOriginalName(file.getOriginalFilename());
                    img.setContentType(file.getContentType());
                    existingProduct.addImage(img);
                }
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);

    }

    @Override
    @Transactional
    public void deleteProduct(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (product.getImages() != null) {
            for (ProductImage image : product.getImages()) {
                // Publicamos evento para cada imagen del producto
                eventPublisher.publishEvent(new ImageDeletedEvent(image.getImagePath()));
            }
        }
        // Al ejecutar delete, el Listener esperará a que esta línea termine con éxito
        productRepository.delete(product);
        logger.warn("Producto eliminado con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public String getProductImageContentTypeByImagePath(String imagePath) {
        return productImageRepository.findContentTypeByImagePath(imagePath)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta de imagen no encontrada"));
    }


    // Métodos Auxiliares para limpiar el código principal

    private Set<Category> resolveCategories(Set<Long> ids) {
        Set<Category> categories = new HashSet<>();
        if (ids != null) {
            ids.stream().filter(Objects::nonNull).forEach(id -> {
                categories.add(categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id)));
            });
        }
        return categories;
    }

    private Set<Feature> resolveFeatures(Set<Long> ids) {
        Set<Feature> features = new HashSet<>();
        if (ids != null) {
            ids.stream().filter(Objects::nonNull).forEach(id -> {
                features.add(featureRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id)));
            });
        }
        return features;
    }

    private void updateCategories(Product product, Set<Long> newIds) {
        if (newIds == null) return;
        Set<Long> finalIds = newIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        // Eliminar las que no están
        product.getCategories().removeIf(c -> !finalIds.contains(c.getId()));

        // Agregar las nuevas
        for (Long id : finalIds) {
            if (product.getCategories().stream().noneMatch(c -> c.getId().equals(id))) {
                product.getCategories().add(categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id)));
            }
        }
    }

    private void updateFeatures(Product product, Set<Long> newIds) {
        if (newIds == null) return;
        Set<Long> finalIds = newIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        product.getFeatures().removeIf(f -> !finalIds.contains(f.getId()));

        for (Long id : finalIds) {
            if (product.getFeatures().stream().noneMatch(f -> f.getId().equals(id))) {
                product.getFeatures().add(featureRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id)));
            }
        }
    }

    private ProductResponseDTO convertToDto(Product product) {
        List<CategoryResponseForProductDTO> categoryResponseForProductDtos = new ArrayList<>();
        List<FeatureResponseDTO> featureResponseForProductDtos = new ArrayList<>();
        List<ImageDTO> imageDtos;

        CategoryResponseForProductDTO categoryResponseForProductDto;
        FeatureResponseDTO featureResponseForProductDto;
        ImageDTO icon;

        if (product.getCategories() != null) {
            for (Category category : product.getCategories()) {
                if (category != null) {
                    categoryResponseForProductDto = new CategoryResponseForProductDTO(
                            category.getId(),
                            category.getName()
                    );
                    categoryResponseForProductDtos.add(categoryResponseForProductDto);
                }
            }
        }

        if (product.getFeatures() != null) {
            for (Feature feature : product.getFeatures()) {
                if (feature != null) {
                    icon = new ImageDTO();
                    icon.setId(feature.getIcon().getId());
                    icon.setImagePath(feature.getIcon().getImagePath());
                    icon.setOriginalName(feature.getIcon().getOriginalName());
                    icon.setContentType(feature.getIcon().getContentType());
                    featureResponseForProductDto = new FeatureResponseDTO(
                            feature.getId(),
                            feature.getName(),
                            icon
                    );
                    featureResponseForProductDtos.add(featureResponseForProductDto);
                }
            }
        }

        imageDtos = new ArrayList<>();
        ImageDTO productImageDto = null;
        if (product.getImages() != null) {
            for (ProductImage productImage : product.getImages()) {
                if (productImage != null) {
                    productImageDto = new ImageDTO();
                    productImageDto.setId(productImage.getId());
                    productImageDto.setImagePath(productImage.getImagePath());
                    productImageDto.setOriginalName(productImage.getOriginalName());
                    productImageDto.setContentType(productImage.getContentType());
                    imageDtos.add(productImageDto);
                }
            }
        }
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                categoryResponseForProductDtos,
                featureResponseForProductDtos,
                imageDtos
        );
    }
}

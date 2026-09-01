package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.event.CategoryUpdatedEvent;
import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.exception.*;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IFinancialConfigurationService;
import com.carlikeafriend_backend.backend.service.IProductService;
import com.carlikeafriend_backend.backend.specification.ProductSpecifications;
import com.carlikeafriend_backend.backend.util.DateValidationUtils;
import com.carlikeafriend_backend.backend.util.FileValidationUtils;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private static final int MAX_IMAGES_PER_PRODUCT = 5; // Definir el límite de imágenes

    private final IProductRepository productRepository;
    private final IFileStorageService fileStorageService;
    private final IFinancialConfigurationService financialConfigService;
    private final IBranchTransferFeeRepository transferFeeRepository;
    private final IProductImageRepository productImageRepository;
    private final ICategoryRepository categoryRepository;
    private final IFeatureRepository featureRepository;
    private final IPolicyRepository policyRepository;
    private final IMakeRepository makeRepository;
    private final FileValidationUtils fileValidationUtils; // Inyección de utilidad
    private final ApplicationEventPublisher eventPublisher;


    @Autowired
    public ProductService(IProductRepository productRepository,
                          IFileStorageService fileStorageService,
                          IFinancialConfigurationService financialConfigService,
                          IBranchTransferFeeRepository transferFeeRepository,
                          IProductImageRepository productImageRepository,
                          ICategoryRepository categoryRepository,
                          IFeatureRepository featureRepository,
                          IPolicyRepository policyRepository,
                          IMakeRepository makeRepository,
                          FileValidationUtils fileValidationUtils,
                          ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
        this.financialConfigService = financialConfigService;
        this.transferFeeRepository = transferFeeRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.featureRepository = featureRepository;
        this.policyRepository = policyRepository;
        this.makeRepository = makeRepository;
        this.fileValidationUtils = fileValidationUtils;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Transactional
    public ProductResponseDTO saveProduct(ProductDTO productDTO, List<MultipartFile> imageFiles) throws
            DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException {

        String productName = StringUtils.capitalize(productDTO.getName());

        logger.info("Intentando guardar nuevo producto: {}", productName);

        // Validación de duplicados por nombre
        if (productRepository.existsByNameAndDeletedFalse(productName)) {
            logger.warn("Ya existe un producto activo con el nombre: {}", productName);
            throw new DuplicateResourceException("Ya existe un producto activo con el nombre: " + productName);
        }

        // Validaciones de imagen
        fileValidationUtils.validateAtLeastOneImage(imageFiles);
        fileValidationUtils.validateImageFiles(imageFiles);
        long validImagesCount = imageFiles.stream().filter(f -> f != null && !f.isEmpty() && f.getSize() > 0).count();
        fileValidationUtils.validateImageCount((int) validImagesCount, MAX_IMAGES_PER_PRODUCT);


        // Mapear DTO a Entidad
        Product product = new Product();
        product.setName(productName);
        product.setDescription(productDTO.getDescription());
        product.setPassengerCapacity(productDTO.getPassengerCapacity());
        product.setBaggageCapacity(productDTO.getBaggageCapacity());
        product.setNumberOfDoors(productDTO.getNumberOfDoors());

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN
        updateMakeAssociation(product, productDTO.getMakeId());
        updateCategories(product, productDTO.getCategories());
        updateFeatures(product, productDTO.getFeatures());
        updatePolicies(product, productDTO.getPolicies());

        // Una vez que se han Asignado las categorías al producto, calcular las finanzas
        updateProductFinancialsBasedOnCategories(product);

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
        return mapToProductDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        logger.info("Buscando todos los productos.");
        return productRepository.findAllByDeletedFalse().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getHomeCatalog() {
        logger.info("Cargando catálogo global para el Home");

        // Ordenamos por mejor calificación y luego por precio
        Sort sort = Sort.by(Sort.Direction.DESC, "averageRating")
                .and(Sort.by(Sort.Direction.ASC, "price"));

        Specification<Product> spec = ProductSpecifications.base()
                .and(ProductSpecifications.isGloballyAvailable());


        return productRepository.findAll(spec, sort).stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getCatalogPriceRange() {
        List<Product> products = productRepository.findAllByDeletedFalse();

        double minPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .min()
                .orElse(0.0);

        double maxPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .max()
                .orElse(150000.0); // Valor de respaldo por si el catálogo está vacío

        Map<String, Double> range = new HashMap<>();
        range.put("minPrice", minPrice);
        range.put("maxPrice", maxPrice);

        return range;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllRecommendedProducts() {
        logger.info("Buscando productos recomendados.");
        return productRepository.findTopRecommendedProducts().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getProductById(Long id) {
        logger.info("Buscando producto con ID: {}", id);
        return productRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToProductDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllFilteredProducts(List<Long> categoryIds,
                                                           List<Long> featureIds,
                                                           Long branchId,
                                                           Long returnBranchId,
                                                           LocalDateTime pickupDate,
                                                           LocalDateTime returnDate,
                                                           Double minPrice,
                                                           Double maxPrice,
                                                           String sortBy) {

        logger.info("Filtrando productos con cálculo dinámico en DB...");

        // 1. Creamos una lista para almacenar las especificaciones dinámicas
        List<Specification<Product>> specs = new ArrayList<>();

        // 2. Agregamos condiciones a la lista solo si los datos existen
        if (categoryIds != null && !categoryIds.isEmpty()) {
            specs.add(ProductSpecifications.inAllCategories(categoryIds));
        }

        if (featureIds != null && !featureIds.isEmpty()) {
            specs.add(ProductSpecifications.inAllFeatures(featureIds));
        }

        boolean hasDateAndBranchFilters = branchId != null && pickupDate != null && returnDate != null;

        if (hasDateAndBranchFilters) {
            // Traemos la configuración financiera
            FinancialConfigurationResponseDTO config = financialConfigService.getConfiguration();

            // Uso de la utilidad centralizada para validar las fechas de búsqueda
            DateValidationUtils.validateBookingDates(pickupDate, returnDate, config, "En Búsqueda");
            specs.add(ProductSpecifications.isAvailableInBranchAndDates(branchId, pickupDate, returnDate));

            // LÓGICA DE PRECIO DINÁMICO (Cuando hay fechas)
            if (minPrice != null && maxPrice != null) {
                long rentalDays = DateValidationUtils.calculateRentalDays(pickupDate, returnDate);

                // Costos fijos (Asumimos el seguro básico para mostrar el catálogo inicial)
                double insuranceCost = config.getInsuranceBasicRate() * rentalDays;

                // Lógica de tarifa de retorno (Transfer Fee)
                double transferFee = 0.0;
                if (returnBranchId != null && !branchId.equals(returnBranchId)) {
                    transferFee = transferFeeRepository
                            .findByOriginBranchIdAndDestinationBranchId(branchId, returnBranchId)
                            .map(BranchTransferFee::getFeeAmount)
                            .orElse(config.getDefaultTransferFee());
                }

                double constantFees = insuranceCost + transferFee;

                // Usamos la nueva especificación
                specs.add(ProductSpecifications.dynamicPriceBetween(
                        minPrice,
                        maxPrice,
                        rentalDays,
                        constantFees,
                        config.getTaxRate()
                ));
            }
        } else {
            // Si no está buscando fechas específicas, POR DEFECTO solo mostramos
            // el catálogo que tiene inventario real disponible en el sistema.
            specs.add(ProductSpecifications.isGloballyAvailable());

            // LÓGICA DE PRECIO ESTÁTICO (Catálogo sin fechas)
            if (minPrice != null && maxPrice != null) {
                // Si el usuario filtra precios en el "Home" sin fechas,
                // buscamos usando el método estático tradicional.
                specs.add(ProductSpecifications.priceBetween(minPrice, maxPrice));
            }
        }


        // 3. Reducimos la lista usando la especificación base como "identidad" (punto de partida).
        // Si no hay filtros en 'specs', finalSpec será exactamente ProductSpecifications.base().
        // Esto elimina la necesidad de usar .where() y .orElse(Specification.allOf()).
        Specification<Product> finalSpec = specs.stream()
                .reduce(ProductSpecifications.base(), Specification::and);

        // 4. Lógica de Ordenamiento. Ahora pasamos el string compuesto "campo_direccion"
        Sort sort = ProductSpecifications.createSort(sortBy);


        // 5. Consulta al Repositorio con Spec + Sort
        return productRepository.findAll(finalSpec, sort).stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> newImageFiles, List<Long> imagesToDeleteIds) throws DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException {
        logger.info("Intentando actualizar producto con ID: {}", id);

        String productName = StringUtils.capitalize(productDTO.getName());

        Product existingProduct = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Producto no encontrado con ID: " + id));


        // Validar que el nombre del producto sea único, excluyendo el producto actual
        if (productName != null && !productName.equals(StringUtils.capitalize(existingProduct.getName()))) {
            if (productRepository.existsByNameAndIdNotAndDeletedFalse(productName, id)) {
                throw new DuplicateResourceException("El nombre " + productName + " ya está en uso por otro producto activo.");
            }
            existingProduct.setName(productName);
        }

        // Actualizar datos básicos del producto
        Optional.ofNullable(productDTO.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(productDTO.getPassengerCapacity()).ifPresent(existingProduct::setPassengerCapacity);
        Optional.ofNullable(productDTO.getBaggageCapacity()).ifPresent(existingProduct::setBaggageCapacity);
        Optional.ofNullable(productDTO.getNumberOfDoors()).ifPresent(existingProduct::setNumberOfDoors);

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN (Limpia y reasigna)
        updateMakeAssociation(existingProduct, productDTO.getMakeId());

        if (productDTO.getCategories() != null) {
            updateCategories(existingProduct, productDTO.getCategories());
        }
        if (productDTO.getFeatures() != null) {
            updateFeatures(existingProduct, productDTO.getFeatures());
        }
        if (productDTO.getPolicies() != null) {
            updatePolicies(existingProduct, productDTO.getPolicies());
        }

        // Una vez que se han Asignado las categorías al producto, calcular las finanzas
        updateProductFinancialsBasedOnCategories(existingProduct);

        // Lógica de Imágenes
        handleImageUpdates(existingProduct, newImageFiles, imagesToDeleteIds);

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductDto(updatedProduct);

    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (product.hasActiveVehicles()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen vehículos activos asociados a este producto.");
        }

        // Limpiar favoritos (Eliminación física de la tabla intermedia)
        // Esto asegura que el producto borrado no aparezca en las listas de deseos de los usuarios
        product.clearAllFavorites();

        // Renombramiento estratégico
        // Esto permite que el nombre original pueda ser usado de nuevo en otro producto
        String timestamp = String.valueOf(System.currentTimeMillis());
        product.setName(product.getName() + "_DELETED_" + timestamp);

        // Ejecutar Borrado Lógico
        product.setDeleted(true);

        productRepository.save(product);
        logger.warn("Producto con ID: {} borrado lógicamente.", id);


/*
        // --- PROTECCIÓN DE INTEGRIDAD (RESTRICTIVO) ---
        validateEmpty(product.getVehicles(), "vehículos", id);

        // Publicar eventos para borrado de imágenes
        if (product.getImages() != null) {
            for (ProductImage image : product.getImages()) {
                // Publicamos evento para cada imagen del producto
                eventPublisher.publishEvent(new ImageDeletedEvent(image.getImagePath()));
            }
        }

        // LIMPIEZA DE MEMORIA: Desvincular antes de borrar

        // 1. Limpiar Marca
        if (product.getMake() != null) {
            product.getMake().removeProduct(product);
        }

        // 2. Limpiar Categorías (Bidireccional)
        updateCategories(product, new HashSet<>()); // Pasa set vacío para limpiar todo

        // 3. Limpiar Características (Bidireccional)
        updateFeatures(product, new HashSet<>());   // Pasa set vacío para limpiar todo

        // 4. Limpiar Políticas (Bidireccional)
        updatePolicies(product, new HashSet<>());   // Pasa set vacío para limpiar todo

        // 5. Elimina de un solo golpe todas las relaciones en la tabla 'user_favorites'
        // asociadas a este producto antes de que el producto sea borrado.
        product.clearAllFavorites();

        // Al ejecutar delete, el Listener esperará a que esta línea termine con éxito
        productRepository.delete(product);
        logger.warn("Producto eliminado con ID: {}", id);

 */
    }

    @Override
    @Transactional(readOnly = true)
    public String getProductImageContentTypeByImagePath(String imagePath) {
        return productImageRepository.findContentTypeByImagePath(imagePath)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta de imagen no encontrada"));
    }

    @EventListener
    @Transactional
    public void handleCategoryUpdate(CategoryUpdatedEvent event) {
        logger.info("Recibido evento de actualización para la Categoría ID: {}. Recalculando finanzas de los productos...", event.getCategoryId());
        // 1. Buscar todos los productos que tienen esta categoría
        List<Product> affectedProducts = productRepository.findAllByCategoriesIdAndDeletedFalse(event.getCategoryId());

        if (!affectedProducts.isEmpty()) {
            // 2. Iteramos y recalculamos AMBOS valores (Precio y Depósito Base)
            // Nota: Como estamos en @Transactional, Hibernate ya trajo las categorías asociadas
            for (Product product : affectedProducts) {
                updateProductFinancialsBasedOnCategories(product);
            }
            // 3. Guardamos los cambios en lote
            productRepository.saveAll(affectedProducts);
            logger.info("Se han actualizado las finanzas de {} productos asociados a la Categoría ID: {}",
                    affectedProducts.size(),
                    event.getCategoryId());
        }

    }


    // --- MÉTODOS AUXILIARES ---
    // Gestionan la integridad bidireccional y limpieza

    private void updateMakeAssociation(Product product, Long makeId) {
        if (makeId == null) return;

        // Desvincular de la marca anterior (si existe)
        if (product.getMake() != null) {
            product.getMake().removeProduct(product);
        }

        // Vincular nueva
        Make newMake = makeRepository.findById(makeId)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con ID: " + makeId));

        newMake.addProduct(product); // Método de conveniencia de Make
    }

    private void updateProductFinancialsBasedOnCategories(Product product) {
        if (product.getCategories() == null || product.getCategories().isEmpty()) {
            product.setPrice(0.0);
            product.setBaseDepositAmount(0.0);
            return;
        }

        // Lógica: Buscar la categoría completa con mayor prioridad
        Category dominantCategory = product.getCategories().stream()
                .max(Comparator.comparingInt(Category::getPriority)) // Mayor número = mayor prioridad
                .orElse(null);

        if (dominantCategory != null) {
            product.setPrice(dominantCategory.getBaseDailyRate());
            // Asignamos el depósito base de la categoría dominante
            product.setBaseDepositAmount(dominantCategory.getBaseDepositAmount() != null ?
                    dominantCategory.getBaseDepositAmount() : 0.0);
        } else {
            product.setPrice(0.0);
            product.setBaseDepositAmount(0.0);
        }
    }

    private void updateCategories(Product product, Set<Long> newIds) {
        // 1. Limpieza Bidireccional: Avisar a las categorías actuales que ya no tienen este producto
        if (product.getCategories() != null) {
            // Copia para evitar ConcurrentModificationException
            new ArrayList<>(product.getCategories()).forEach(cat -> {
                cat.removeProduct(product); // Usamos el método de conveniencia para borrar
            });
        }

        // 2. Asignación
        if (newIds != null && !newIds.isEmpty()) {
            newIds.stream().filter(Objects::nonNull).forEach(id -> {
                Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
                category.addProduct(product); // Sincroniza ambos lados
            });
        }
    }

    private void updateFeatures(Product product, Set<Long> newIds) {
        // 1. Limpieza Bidireccional
        if (product.getFeatures() != null) {
            new ArrayList<>(product.getFeatures()).forEach(feat -> {
                feat.removeProduct(product); // Usamos método de conveniencia
            });
            //product.getFeatures().clear();
        }

        // 2. Asignación
        if (newIds != null && !newIds.isEmpty()) {
            newIds.stream().filter(Objects::nonNull).forEach(id -> {
                Feature feature = featureRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id));
                feature.addProduct(product); // Sincroniza ambos lados
            });
        }
    }

    private void updatePolicies(Product product, Set<Long> newIds) {
        // 1. Limpieza Bidireccional
        if (product.getPolicies() != null) {
            new ArrayList<>(product.getPolicies()).forEach(policy -> {
                policy.removeProduct(product); // Usamos método de conveniencia
            });

        }

        // 2. Asignación
        if (newIds != null && !newIds.isEmpty()) {
            newIds.stream().filter(Objects::nonNull).forEach(id -> {
                Policy policy = policyRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Política no encontrada con ID: " + id));
                policy.addProduct(product); // Sincroniza ambos lados
            });
        }
    }

    private void handleImageUpdates(Product product, List<MultipartFile> newImageFiles, List<Long> imagesToDeleteIds) throws IOException, ImageLimitExceededException, InvalidFileExtensionException {
        int currentCount = product.getImages().size();
        int deleteCount = (imagesToDeleteIds != null) ? imagesToDeleteIds.size() : 0;

        // Validación de nuevas imágenes
        if (newImageFiles != null) {
            fileValidationUtils.validateImageFiles(newImageFiles);
        }
        int newCount = (newImageFiles != null) ? (int) newImageFiles.stream().filter(f -> !f.isEmpty()).count() : 0;
        int finalCount = currentCount - deleteCount + newCount;

        if (finalCount == 0) throw new ImageLimitExceededException("El producto debe tener al menos una imagen.");

        // Validar máximo
        fileValidationUtils.validateImageCount(finalCount, MAX_IMAGES_PER_PRODUCT);

        // Eliminar imágenes
        if (imagesToDeleteIds != null && !imagesToDeleteIds.isEmpty()) {
            List<ProductImage> imagesToRemove = product.getImages().stream()
                    .filter(img -> imagesToDeleteIds.contains(img.getId()))
                    .collect(Collectors.toList());

            for (ProductImage image : imagesToRemove) {
                // Disparamos el evento. El Listener borrará el archivo SOLO si el commit es exitoso.
                eventPublisher.publishEvent(new ImageDeletedEvent(image.getImagePath()));
                product.removeImage(image);
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
                    product.addImage(img);
                }
            }
        }
    }

    private ProductResponseDTO mapToProductDto(Product product) {
        List<SimpleResponseDTO> categoryDtos = product.getCategories() != null
                ? product.getCategories().stream().map(c -> new SimpleResponseDTO(c.getId(), c.getName())).collect(Collectors.toList())
                : new ArrayList<>();

        List<FeatureResponseDTO> featureDtos = new ArrayList<>();
        if (product.getFeatures() != null) {
            for (Feature f : product.getFeatures()) {
                ImageDTO icon = f.getIcon() != null
                        ? new ImageDTO(f.getIcon().getId(), f.getIcon().getImagePath(), f.getIcon().getOriginalName(), f.getIcon().getContentType())
                        : null;
                featureDtos.add(new FeatureResponseDTO(
                        f.getId(),
                        f.getName(),
                        icon
                ));
            }
        }

        List<PolicyCompleteResponseDTO> policyDtos = product.getPolicies() != null
                ? product.getPolicies().stream().map(p -> new PolicyCompleteResponseDTO(p.getId(), p.getName(), p.getContent())).collect(Collectors.toList())
                : new ArrayList<>();

        List<ImageDTO> imageDtos = product.getImages() != null
                ? product.getImages().stream().map(i -> new ImageDTO(i.getId(), i.getImagePath(), i.getOriginalName(), i.getContentType())).collect(Collectors.toList())
                : new ArrayList<>();

        SimpleResponseDTO makeDto = product.getMake() != null
                ? new SimpleResponseDTO(product.getMake().getId(), product.getMake().getName())
                : null;

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                makeDto,
                product.getDescription(),
                product.getPassengerCapacity(),
                product.getBaggageCapacity(),
                product.getNumberOfDoors(),
                categoryDtos,
                featureDtos,
                product.getPrice(),
                product.getBaseDepositAmount(),
                imageDtos,
                policyDtos,
                product.getAverageRating(),
                product.getTotalReviews()
        );
    }


}

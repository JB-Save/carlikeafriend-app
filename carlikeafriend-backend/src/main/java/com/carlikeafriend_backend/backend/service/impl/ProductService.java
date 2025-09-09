package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.entity.Image;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.UniqueProductException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private static final int MAX_IMAGES_PER_PRODUCT = 5; // Definir el límite de imágenes

    private IProductRepository productRepository;

    private IFileStorageService fileStorageService;

    @Autowired
    public ProductService(IProductRepository productRepository, IFileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }


    @Override
    @Transactional
    public Product saveProduct(ProductDTO productDto, List<MultipartFile> images) {
        // Validación de duplicados por nombre
        if (productRepository.findByName(productDto.getName()).isPresent()) {
            throw new UniqueProductException("Ya existe un producto con el nombre: " + productDto.getName());
        }

        // Validación del límite de imágenes al crear
        if(images != null && images.size() > MAX_IMAGES_PER_PRODUCT){
            throw new ImageLimitExceededException("No se pueden subir más de " + MAX_IMAGES_PER_PRODUCT + " imágenes por producto.");
        }

        // Mapear DTO a Entidad
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());

        if (images != null) {
            for (MultipartFile imageFile : images) {

                if (!imageFile.isEmpty()) {
                    String imagePath = fileStorageService.storeFile(imageFile);
                    Image image = new Image();
                    image.setImagePath(imagePath);
                    image.setOriginalName(imageFile.getOriginalFilename());
                    image.setContentType(imageFile.getContentType());
                    product.addImage(image);
                }
            }
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDto, List<MultipartFile> newImages, List<Long> imagesToDeleteIds) {
        return productRepository.findById(id).map(existingProduct -> {
            // Validar unicidad del nombre si cambia
            if (!productDto.getName().equals(existingProduct.getName())) {
                if (productRepository.findByName(productDto.getName()).isPresent()) {
                    throw new UniqueProductException("Ya existe otro producto con el nombre: " + productDto.getName());
                }
            }
            // Actualizar datos básicos del producto
            existingProduct.setName(productDto.getName());
            existingProduct.setDescription(productDto.getDescription());
            existingProduct.setPrice(productDto.getPrice());

            // Calcular el número de imágenes después de la operación
            int currentImagesCount = existingProduct.getImages().size();
            int imagesAfterDeletion = currentImagesCount - (imagesToDeleteIds != null ? imagesToDeleteIds.size() : 0);
            int totalImagesAfterUpdate = imagesAfterDeletion + (newImages != null ? newImages.size() : 0);

            if(totalImagesAfterUpdate > MAX_IMAGES_PER_PRODUCT){
                throw new ImageLimitExceededException("El producto no puede tener más de " + MAX_IMAGES_PER_PRODUCT + " imágenes. Actualmente tendría " + totalImagesAfterUpdate + ".");
            }

            // Eliminar imágenes existentes
            if (imagesToDeleteIds != null && !imagesToDeleteIds.isEmpty()) {
                List<Image> imagesToRemove = existingProduct.getImages().stream()
                        .filter(img -> imagesToDeleteIds.contains(img.getId()))
                        .collect(Collectors.toList());

                for (Image image : imagesToRemove) {
                    fileStorageService.deleteFile(image.getImagePath());
                    existingProduct.removeImage(image);
                }
            }

            // Añadir nuevas imágenes
            if (newImages != null && !newImages.isEmpty()) {
                for (MultipartFile newImageFile : newImages) {
                    if (!newImageFile.isEmpty()) {
                        String imagePath = fileStorageService.storeFile(newImageFile);
                        Image image = new Image();
                        image.setImagePath(imagePath);
                        image.setOriginalName(newImageFile.getOriginalFilename());
                        image.setContentType(newImageFile.getContentType());
                        existingProduct.addImage(image);
                    }
                }
            }
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Product patchProduct(Long id, ProductDTO partialProductDto, List<MultipartFile> newImages, List<Long> imagesToDeleteIds) {
        return productRepository.findById(id).map(existingProduct -> {
            // Actualizar datos básicos del producto (solo si se proporcionan)
            if (partialProductDto.getName() != null && !existingProduct.getName().isEmpty()) {
                if (!partialProductDto.getName().equals(existingProduct.getName())) {
                    if (productRepository.findByName(partialProductDto.getName()).isPresent()) {
                        throw new UniqueProductException("Ya existe otro producto con el nombre: " + partialProductDto.getName());
                    }
                }
                existingProduct.setName(partialProductDto.getName());
            }

            if (partialProductDto.getDescription() != null && !existingProduct.getDescription().isEmpty()) {
                existingProduct.setDescription(partialProductDto.getDescription());
            }

            // Para el precio, si el DTO tiene un valor diferente de 0.0 (asumiendo 0.0 es un valor por defecto no enviado)
            if (partialProductDto.getPrice() != 0.0) {
                existingProduct.setPrice(partialProductDto.getPrice());
            }

            // Calcular el número de imágenes después de la operación
            int currentImagesCount = existingProduct.getImages().size();
            int imagesAfterDeletion = currentImagesCount - (imagesToDeleteIds != null ? imagesToDeleteIds.size() : 0);
            int totalImagesAfterUpdate = imagesAfterDeletion + (newImages != null ? newImages.size() : 0);

            if (totalImagesAfterUpdate > MAX_IMAGES_PER_PRODUCT) {
                throw new ImageLimitExceededException("El producto no puede tener más de " + MAX_IMAGES_PER_PRODUCT + " imágenes. Actualmente tendría " + totalImagesAfterUpdate + ".");
            }

            // Eliminar imágenes existentes
            if (imagesToDeleteIds != null && !imagesToDeleteIds.isEmpty()) {
                List<Image> imagesToRemove = existingProduct.getImages().stream()
                        .filter(img -> imagesToDeleteIds.contains(img.getId()))
                        .collect(Collectors.toList());

                for (Image image : imagesToRemove) {
                    fileStorageService.deleteFile(image.getImagePath());
                    existingProduct.removeImage(image);
                }
            }

            // Añadir nuevas imágenes
            if (newImages != null && !newImages.isEmpty()) {
                for (MultipartFile newImageFile : newImages) {
                    if (!newImageFile.isEmpty()) {
                        String imagePath = fileStorageService.storeFile(newImageFile);
                        Image image = new Image();
                        image.setImagePath(imagePath);
                        image.setOriginalName(newImageFile.getOriginalFilename());
                        image.setContentType(newImageFile.getContentType());
                        existingProduct.addImage(image);
                    }
                }
            }
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            for (Image image : product.getImages()) {
                fileStorageService.deleteFile(image.getImagePath());
            }
            productRepository.delete(product);
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }

    }
}

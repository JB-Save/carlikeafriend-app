package com.carlikeafriend_backend.backend.controller;

import org.springframework.core.io.Resource;
import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.UniqueProductException;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carlikeafriend")
public class ProductController {

    private IProductService productService;

    private IFileStorageService fileStorageService;

    @Autowired
    public ProductController(IProductService productService, IFileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(@RequestPart("product") @Valid ProductDTO productDTO,
                                                 @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Product savedProduct = productService.saveProduct(productDTO, images);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/recommended-products")
    public ResponseEntity<List<Product>> getAllRecommendedProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

   @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "imagesToDelete", required = false) List<Long> imagesToDeleteIds) {

            Product updated = productService.updateProduct(id, productDTO, newImages, imagesToDeleteIds);
            return new ResponseEntity<>(updated, HttpStatus.OK);

    }

    @PatchMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> patchProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductDTO partialProductDto,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "imagesToDelete", required = false) List<Long> imagesToDeleteIds) {

            Product patched = productService.patchProduct(id, partialProductDto, newImages, imagesToDeleteIds);
            return new ResponseEntity<>(patched, HttpStatus.OK);
    }

    @GetMapping("/products/images/image/{fileName:.+}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Lógica para determinar el tipo de contenido (mime type)
        String contentType = "application/octet-stream";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            contentType = "image/gif";
        } else if (fileName.endsWith(".svg")) {
            contentType = "image/svg+xml";
        } else if (fileName.endsWith(".webp")) {
            contentType = "image/webp";
        } else {
            // Valor por defecto en caso de no coincidencia
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UniqueProductException.class)
    public ResponseEntity<String> handleUniqueProductException(UniqueProductException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ImageLimitExceededException.class)
    public ResponseEntity<String> handleImageLimitExceededException(ImageLimitExceededException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        System.err.println("Error de IO: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // En el caso de "Producto no encontrado", devolvemos 404
        if (ex.getMessage() != null && ex.getMessage().contains("Producto no encontrado") || ex.getMessage().contains("El archivo no se pudo encontrar")) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        // Para otras RuntimeException, devolvemos 500 Internal Server Error
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ProductResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import org.springframework.core.io.Resource;
import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import com.carlikeafriend_backend.backend.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class ProductController {

    private final IProductService productService;
    private final IFileStorageService fileStorageService;

    @Autowired
    public ProductController(IProductService productService, IFileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> saveProduct(@RequestPart("product") @Valid ProductDTO productDTO,
                                                          @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles)
            throws DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException {
        ProductResponseDTO savedProduct = productService.saveProduct(productDTO, imageFiles);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/products/home-catalogues")
    public ResponseEntity<List<ProductResponseDTO>> getHomeCatalog() {
        return new ResponseEntity<>(productService.getHomeCatalog(), HttpStatus.OK);
    }

    @GetMapping("/products/price-ranges")
    public ResponseEntity<Map<String, Double>> getCatalogPriceRange() {
        return new ResponseEntity<>(productService.getCatalogPriceRange(), HttpStatus.OK);
    }

    @GetMapping("/products/filters")
    public ResponseEntity<List<ProductResponseDTO>> getFilteredProducts(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> featureIds,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long returnBranchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "price_asc") String sortBy
    ) {

        List<ProductResponseDTO> filteredProducts = productService.getAllFilteredProducts(
                categoryIds,
                featureIds,
                branchId,
                returnBranchId,
                pickupDate,
                returnDate,
                minPrice,
                maxPrice,
                sortBy);

        return new ResponseEntity<>(filteredProducts, HttpStatus.OK);

    }

    @GetMapping("/products/recommended-products")
    public ResponseEntity<List<ProductResponseDTO>> getAllRecommendedProducts() {
        return new ResponseEntity<>(productService.getAllRecommendedProducts(), HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        Optional<ProductResponseDTO> productDTO = productService.getProductById(id);
        return productDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/products/{id}")
    //@PatchMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "newImageFiles", required = false) List<MultipartFile> newImageFiles,
            @RequestPart(value = "imagesToDelete", required = false) List<Long> imagesToDeleteIds)
            throws DuplicateResourceException, ImageLimitExceededException, IOException {

        ProductResponseDTO updatedProduct = productService.updateProduct(id, productDTO, newImageFiles, imagesToDeleteIds);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) throws IOException {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/products/images/image/product_folder/{fileName:.+}")
    public ResponseEntity<Resource> getImageFile(@PathVariable String fileName) throws IOException {
        Resource file = fileStorageService.loadFileAsResource(fileName, "product_folder");
        // Lógica para determinar el tipo de contenido (mime type)
        String contentType = productService.getProductImageContentTypeByImagePath("/image/product_folder/" + fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

}

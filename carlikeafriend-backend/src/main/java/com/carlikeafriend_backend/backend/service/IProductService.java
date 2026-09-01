package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.dto.ProductResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProductService {

    ProductResponseDTO saveProduct(ProductDTO productDto, List<MultipartFile> imageFiles) throws DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException;

    List<ProductResponseDTO> getAllProducts();

    List<ProductResponseDTO> getHomeCatalog();

    Map<String, Double> getCatalogPriceRange();

    List<ProductResponseDTO> getAllRecommendedProducts();

    Optional<ProductResponseDTO> getProductById(Long id);

    List<ProductResponseDTO> getAllFilteredProducts(List<Long> categoryIds,
                                                    List<Long> featureIds,
                                                    Long branchId,
                                                    Long returnBranchId,
                                                    LocalDateTime pickupDate,
                                                    LocalDateTime returnDate,
                                                    Double minPrice,
                                                    Double maxPrice,
                                                    String sortBy);

    ProductResponseDTO updateProduct(Long id, ProductDTO productDto, List<MultipartFile> newImages, List<Long> imagesToDeleteIds) throws DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException;

    void deleteProduct(Long id);

    String getProductImageContentTypeByImagePath(String imagePath);
}

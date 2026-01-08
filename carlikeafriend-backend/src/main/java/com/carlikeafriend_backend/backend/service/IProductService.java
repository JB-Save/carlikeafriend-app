package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.dto.ProductResponseDTO;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IProductService {

    ProductResponseDTO saveProduct(ProductDTO productDto, List<MultipartFile> imageFiles) throws UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException;

    List<ProductResponseDTO> findAllProducts();

    Optional<ProductResponseDTO> findProductById(Long id);

    List<ProductResponseDTO> findAllFilteredProducts(List<Long> categoryIds, List<Long> featureIds, Double minPrice, Double maxPrice, String sortBy);

    ProductResponseDTO updateProduct(Long id, ProductDTO productDto, List<MultipartFile> newImages, List<Long> imagesToDeleteIds) throws UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException;

    void deleteProduct(Long id) throws IOException;

    String getProductImageContentTypeByImagePath(String imagePath);
}

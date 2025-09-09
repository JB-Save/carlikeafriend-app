package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ProductDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    Product saveProduct(ProductDTO productDto, List<MultipartFile> images);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product updateProduct(Long id, ProductDTO productDto, List<MultipartFile> newImages, List<Long> imagesToDeleteIds);
    Product patchProduct(Long id, ProductDTO partialProductDto, List<MultipartFile> newImages, List<Long> imagesToDeleteIds);
    void deleteProduct(Long id);
}

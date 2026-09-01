package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface ICategoryService {

    CategoryResponseDTO saveCategory(CategoryDTO categoryDTO, MultipartFile imageFile) throws IOException, DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException;

    List<CategoryResponseDTO> getAllCategories();

    Optional<CategoryResponseDTO> getCategoryById(Long id);

    CategoryResponseDTO updateCategory(Long id, CategoryDTO categoryDTO, MultipartFile newImageFile) throws IOException, DuplicateResourceException, InvalidFileExtensionException;

    void deleteCategory(Long id) ;

    String getCategoryImageContentTypeByImagePath(String imagePath);
}

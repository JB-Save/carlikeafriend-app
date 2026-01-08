package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface ICategoryService {

    CategoryResponseDTO saveCategory(CategoryDTO categoryDTO, MultipartFile imageFile) throws IOException, UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException;

    List<CategoryResponseDTO> findAllCategories();

    Optional<CategoryResponseDTO> findCategoryById(Long id);

    CategoryResponseDTO updateCategory(Long id, CategoryDTO categoryDTO, MultipartFile newImageFile) throws IOException, UniqueNameException, InvalidFileExtensionException;

    void deleteCategory(Long id) throws IOException;

    String getCategoryImageContentTypeByImagePath(String imagePath);
}

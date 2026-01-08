package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.CategoryDTO;
import com.carlikeafriend_backend.backend.dto.CategoryResponseDTO;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.service.ICategoryService;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class CategoryController {

    private final ICategoryService categoryService;
    private final IFileStorageService fileStorageService;


    @Autowired
    public CategoryController(ICategoryService categoryService, IFileStorageService fileStoreService) {
        this.categoryService = categoryService;
        this.fileStorageService = fileStoreService;
    }


    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDTO> saveCategory(@RequestPart("category") @Valid CategoryDTO categoryDTO,
                                                            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile)
            throws UniqueNameException, ImageLimitExceededException, InvalidFileExtensionException, IOException {
        CategoryResponseDTO savedCategory = categoryService.saveCategory(categoryDTO, imageFile);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }


    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> findAllCategories() {
        return new ResponseEntity<>(categoryService.findAllCategories(), HttpStatus.OK);
    }


    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> findCategoryById(@PathVariable Long id) {
        Optional<CategoryResponseDTO> categoryDTO = categoryService.findCategoryById(id);
        return categoryDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/categories/{id}")
    //@PatchMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestPart("category") @Valid CategoryDTO categoryDTO,
            @RequestPart(value = "newImageFile", required = false) MultipartFile newImageFile)
            throws UniqueNameException, InvalidFileExtensionException, IOException {

        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryDTO, newImageFile);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) throws IOException {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/categories/images/image/category_folder/{fileName:.+}")
    public ResponseEntity<Resource> getImageFile(@PathVariable String fileName) throws IOException {
        Resource file = fileStorageService.loadFileAsResource(fileName, "category_folder");
        // Lógica para determinar el tipo de contenido (mime type)
        String contentType = categoryService.getCategoryImageContentTypeByImagePath("/image/category_folder/" + fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}

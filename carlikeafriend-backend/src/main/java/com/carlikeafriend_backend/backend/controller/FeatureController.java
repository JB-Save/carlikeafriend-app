package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.FeatureDTO;
import com.carlikeafriend_backend.backend.dto.FeatureResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.service.IFeatureService;
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
public class FeatureController {

    private final IFeatureService featureService;
    private final IFileStorageService fileStorageService;


    @Autowired
    public FeatureController(IFeatureService featureService, IFileStorageService fileStoreService) {
        this.featureService = featureService;
        this.fileStorageService = fileStoreService;
    }


    @PostMapping("/features")
    public ResponseEntity<FeatureResponseDTO> saveFeature(@RequestPart("feature") @Valid FeatureDTO featureDTO,
                                                           @RequestPart(value = "imageFile", required = false) MultipartFile imageFile)
            throws DuplicateResourceException, ImageLimitExceededException, InvalidFileExtensionException, IOException {
        FeatureResponseDTO savedFeature = featureService.saveFeature(featureDTO, imageFile);
        return new ResponseEntity<>(savedFeature, HttpStatus.CREATED);
    }


    @GetMapping("/features")
    public ResponseEntity<List<FeatureResponseDTO>> getAllFeatures() {
        return new ResponseEntity<>(featureService.getAllFeatures(), HttpStatus.OK);
    }


    @GetMapping("/features/{id}")
    public ResponseEntity<FeatureResponseDTO> getFeatureById(@PathVariable Long id) {
        Optional<FeatureResponseDTO> featureDTO = featureService.getFeatureById(id);
        return featureDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/features/{id}")
    //@PatchMapping("/features/{id}")
    public ResponseEntity<FeatureResponseDTO> updateFeature(
            @PathVariable Long id,
            @RequestPart("feature") @Valid FeatureDTO featureDTO,
            @RequestPart(value = "newImageFile", required = false) MultipartFile newImageFile)
            throws DuplicateResourceException, InvalidFileExtensionException, IOException {

        FeatureResponseDTO updatedFeature = featureService.updateFeature(id, featureDTO, newImageFile);
        return new ResponseEntity<>(updatedFeature, HttpStatus.OK);
    }

    @DeleteMapping("/features/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long id) throws IOException {
        featureService.deleteFeature(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/features/images/image/feature_folder/{fileName:.+}")
    public ResponseEntity<Resource> getImageFile(@PathVariable String fileName) throws IOException {
        Resource file = fileStorageService.loadFileAsResource(fileName, "feature_folder");
        // Lógica para determinar el tipo de contenido (mime type)
        String contentType = featureService.getFeatureImageContentTypeByImagePath("/image/feature_folder/" + fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}

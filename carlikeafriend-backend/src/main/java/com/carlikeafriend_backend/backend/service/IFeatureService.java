package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.FeatureDTO;
import com.carlikeafriend_backend.backend.dto.FeatureResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IFeatureService {

    FeatureResponseDTO saveFeature(FeatureDTO featureDTO, MultipartFile imageFile) throws IOException, DuplicateResourceException, ImageLimitExceededException , InvalidFileExtensionException;

    List<FeatureResponseDTO> getAllFeatures();

    Optional<FeatureResponseDTO> getFeatureById(Long id);

    FeatureResponseDTO updateFeature(Long id, FeatureDTO featureDTO, MultipartFile newImageFile) throws IOException, DuplicateResourceException, InvalidFileExtensionException;

    void deleteFeature(Long id) ;

    String getFeatureImageContentTypeByImagePath(String imagePath);
}

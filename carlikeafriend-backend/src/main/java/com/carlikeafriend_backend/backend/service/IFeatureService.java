package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.FeatureDTO;
import com.carlikeafriend_backend.backend.dto.FeatureResponseDTO;
import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface IFeatureService {

    FeatureResponseDTO saveFeature(FeatureDTO featureDTO, MultipartFile imageFile) throws IOException, UniqueNameException, ImageLimitExceededException , InvalidFileExtensionException;

    List<FeatureResponseDTO> findAllFeatures();

    Optional<FeatureResponseDTO> findFeatureById(Long id);

    FeatureResponseDTO updateFeature(Long id, FeatureDTO featureDTO, MultipartFile newImageFile) throws IOException, UniqueNameException, InvalidFileExtensionException;

    void deleteFeature(Long id) throws IOException;

    String getFeatureImageContentTypeByImagePath(String imagePath);
}

package com.carlikeafriend_backend.backend.util;

import com.carlikeafriend_backend.backend.exception.ImageLimitExceededException;
import com.carlikeafriend_backend.backend.exception.InvalidFileExtensionException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidationUtils {

    // Definimos el tamaño máximo lógico (de negocio)
    // Ejemplo: 5MB por imagen es más que suficiente para web
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    public void validateImageFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            if (!isImage(file)) {
                throw new InvalidFileExtensionException("El archivo debe ser una imagen de tipo: JPG, PNG, GIF, WEBP.");
            }
            // Validamos el tamaño
            if (file.getSize() > MAX_FILE_SIZE_BYTES) {
                throw new ImageLimitExceededException("El archivo " + file.getOriginalFilename() + " excede el tamaño máximo permitido de 5MB.");
            }
        }
    }

    public void validateImageFiles(List<MultipartFile> files) {
        if (files != null) {
            for (MultipartFile file : files) {
                validateImageFile(file);
            }
        }
    }

    public void validateImageCount(int currentCount, int maxAllowed) {
        if (currentCount > maxAllowed) {
            throw new ImageLimitExceededException("Límite de imágenes excedido. Máximo permitido: " + maxAllowed);
        }
    }

    public void validateAtLeastOneImage(List<MultipartFile> files) {
        boolean hasValidFile = files != null && files.stream().anyMatch(f -> !f.isEmpty() && f.getSize() > 0);
        if (!hasValidFile) {
            throw new ImageLimitExceededException("Se requiere adjuntar al menos una imagen.");
        }
    }

    public void validateAtLeastOneImage(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw new ImageLimitExceededException("Se requiere adjuntar al menos una imagen.");
        }
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}

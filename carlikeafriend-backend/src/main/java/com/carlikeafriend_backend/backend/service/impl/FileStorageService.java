package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService implements IFileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file){
        // Normaliza el nombre del archivo para evitar problemas de ruta
        // Usamos UUID para asegurar nombres únicos
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetLocation); // Asegura que el directorio de destino exista
            Path filePath = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            // Retorna la ruta relativa que se guardará en la DB (ej. /image/uuid_nombre.jpg)
            return "/image/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + fileName + ". Por favor, intente de nuevo!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName){
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("El archivo no se pudo encontrar o leer: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error en la URL del archivo: " + fileName, ex);
        }
    }

    public void deleteFile(String fileName){
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName.replace("/image/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Registra el error, pero no necesariamente genera una excepción si el archivo no existe
            System.err.println("No se pudo eliminar el archivo " + fileName + ": " + ex.getMessage());
        }
    }
}

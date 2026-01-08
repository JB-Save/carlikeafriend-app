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

    public String storeFile(MultipartFile file, String folder) throws IOException {
        // Normaliza el nombre del archivo para evitar problemas de ruta
        // Usamos UUID para asegurar nombres únicos
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(folder);
            Files.createDirectories(targetLocation); // Asegura que el directorio de destino exista
            Path filePath = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            // Retorna la ruta relativa que se guardará en la DB (ej. /image/folder/uuid_nombre.jpg)
            return "/image/" + folder +"/"+ fileName;
        } catch (IOException ex) {
            throw new IOException("No se pudo almacenar el archivo " + fileName + ". Por favor, intente de nuevo!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, String folder) throws IOException {
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(folder).resolve(fileName);
            // Creamos el recurso desde el URI de la ruta
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("El archivo no se pudo encontrar o leer en la carpeta " + folder +": " +fileName);
            }
        } catch (MalformedURLException ex) {
            throw new IOException("Error en la URL del archivo: " + fileName, ex);
        }
    }

    public void deleteFile(String fullPath) throws IOException {
        try {
            // fullPath viene como "/image/folder/archivo.jpg"
            // Reemplazamos "/image/" por nada para obtener "folder/archivo.jpg"
            String relativePath = fullPath.replace("/image/", "");
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new IOException("No se pudo eliminar el archivo " + fullPath + ": " + ex.getMessage(), ex);
        }
    }
}

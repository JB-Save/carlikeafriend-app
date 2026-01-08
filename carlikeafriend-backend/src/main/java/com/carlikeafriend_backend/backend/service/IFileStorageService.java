package com.carlikeafriend_backend.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileStorageService {

    String storeFile(MultipartFile file, String folder) throws IOException;

    Resource loadFileAsResource(String fileName, String folder) throws IOException;

    void deleteFile(String fileName) throws IOException;
}

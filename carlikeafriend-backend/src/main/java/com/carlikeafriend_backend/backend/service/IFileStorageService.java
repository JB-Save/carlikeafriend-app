package com.carlikeafriend_backend.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {

    String storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
}

package com.fastcampus.loan.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    void save(MultipartFile file);

    Resource load(String fileName);
}

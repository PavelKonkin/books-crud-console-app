package com.books.file.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    ResponseEntity<String> uploadImage(int id, MultipartFile file);

    void downloadImage(int id, HttpServletResponse response);
}

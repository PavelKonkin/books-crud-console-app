package com.books.file.controller;

import com.books.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/v1/files")
public class FileController {
    private  final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/books/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        log.info("Request to upload image for book with id = {} has been received", id);
        ResponseEntity<String> response = fileService.uploadImage(id, file);
        log.info("Image for book with id = {} successfully uploaded", id);
        return response;

    }

    @GetMapping("/books/{id}/image")
    public void downloadImage(@PathVariable int id, HttpServletResponse response) {
        log.info("Request to download image for book with id = {} has been received", id);
        fileService.downloadImage(id, response);
        log.info("Image for book with id = {} successfully downloaded", id);
    }

    @DeleteMapping("/books/{id}/image")
    public ResponseEntity<String> deleteImage(@PathVariable int id) {
        log.info("Request to delete image for book with id = {} has been received", id);
        ResponseEntity<String> response = fileService.deleteImage(id);
        log.info("Image for book with id = {} successfully deleted", id);
        return response;
    }
}

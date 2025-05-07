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
    private  final MessageSource messageSource;

    public FileController(FileService fileService, MessageSource messageSource) {
        this.fileService = fileService;
        this.messageSource = messageSource;
    }

    @PostMapping("/books/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        log.info(messageSource
                .getMessage("uploadImageBeforeMessage", null, LocaleContextHolder.getLocale()), id);
        ResponseEntity<String> response = fileService.uploadImage(id, file);
        log.info(messageSource
                .getMessage("uploadImageSuccessMessage", null, LocaleContextHolder.getLocale()), id);
        return response;

    }

    @GetMapping("/books/{id}/image")
    public void downloadImage(@PathVariable int id, HttpServletResponse response) {
        log.info(messageSource
                .getMessage("downloadImageBeforeMessage", null, LocaleContextHolder.getLocale()), id);
        fileService.downloadImage(id, response);
        log.info(messageSource
                .getMessage("downloadImageSuccessMessage", null, LocaleContextHolder.getLocale()), id);
    }
}

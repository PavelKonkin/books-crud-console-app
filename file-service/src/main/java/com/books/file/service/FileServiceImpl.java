package com.books.file.service;

import com.books.dto.BookDto;
import com.books.file.client.BooksFeignClient;
import com.books.file.repository.FileRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {
    private final BooksFeignClient booksFeignClient;
    private final FileRepository fileRepository;
    private final MessageSource messageSource;


    @Autowired
    public FileServiceImpl(FileRepository fileRepository,
                           BooksFeignClient booksFeignClient, MessageSource messageSource) {
        this.booksFeignClient = booksFeignClient;
        this.fileRepository = fileRepository;
        this.messageSource = messageSource;
    }

    @Override
    public ResponseEntity<String> uploadImage(int id, MultipartFile file) {
        try {
            BookDto book = booksFeignClient.getBook(getJwtToken(), id);
            String imageId = fileRepository.storeFile(file);
            book.setImageId(imageId);
            booksFeignClient.updateBook(getJwtToken(), book);
            return ResponseEntity.ok(messageSource
                    .getMessage("imageUploadSuccess", null, LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource
                    .getMessage("imageUploadError", null, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public void downloadImage(int id, HttpServletResponse response) {
        BookDto book = booksFeignClient.getBook(getJwtToken(), id);
        try {
            fileRepository.downloadFile(book.getImageId(), response);
        } catch (IOException e) {
            throw new RuntimeException(messageSource
                    .getMessage("errorDownloadingFile", null, LocaleContextHolder.getLocale()));
        }
    }

    private String getJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = null;
        if (authentication != null && authentication.getCredentials() != null) {
            jwt = (String) authentication.getCredentials();  // Извлекаем токен из контекста
        }
        return "Bearer " + jwt;
    }
}

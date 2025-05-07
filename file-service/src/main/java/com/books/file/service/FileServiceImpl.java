package com.books.file.service;

import com.books.dto.BookDto;
import com.books.file.client.BooksFeignClient;
import com.books.file.repository.FileRepository;
import com.books.utils.helper.RetryHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_NEW_FILE = "new-file";
    private static final String TOPIC_DELETE_FILE = "delete-file";
    private static final String TOPIC_DELETE_OLD_FILE = "delete-old-file";
    private final ObjectMapper objectMapper;




    @Autowired
    public FileServiceImpl(FileRepository fileRepository,
                           BooksFeignClient booksFeignClient, MessageSource messageSource, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.booksFeignClient = booksFeignClient;
        this.fileRepository = fileRepository;
        this.messageSource = messageSource;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<String> uploadImage(int id, MultipartFile file) {
        try {
            BookDto book = RetryHelper.executeWithRetry(() -> booksFeignClient.getBook(getJwtToken(), id));
            String oldImageId = book.getImageId();
            String imageId = fileRepository.storeFile(file);
            book.setImageId(imageId);
            if (oldImageId != null) {
                kafkaTemplate.send(TOPIC_DELETE_OLD_FILE, oldImageId);
            }
            kafkaTemplate.send(TOPIC_NEW_FILE, objectMapper.writeValueAsString(book));
            return ResponseEntity.ok(messageSource
                    .getMessage("imageUploadSuccess", null, LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource
                    .getMessage("imageUploadError", null, LocaleContextHolder.getLocale()));
        }

    }

    @Override
    public void downloadImage(int id, HttpServletResponse response) {
        BookDto book = RetryHelper.executeWithRetry(() -> booksFeignClient.getBook(getJwtToken(), id));
        try {
            fileRepository.downloadFile(book.getImageId(), response);
        } catch (Exception e) {
            throw new InvalidDataAccessApiUsageException(messageSource
                    .getMessage("errorDownloadingFile", null, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public void delete(String fileId) {
        fileRepository.deleteFile(fileId);
    }

    @Override
    public ResponseEntity<String> deleteImage(int id) {
        try {
            BookDto book = RetryHelper.executeWithRetry(() -> booksFeignClient.getBook(getJwtToken(), id));
            String imageId = book.getImageId();
            delete(imageId);
            book.setImageId(null);
            kafkaTemplate.send(TOPIC_DELETE_FILE, objectMapper.writeValueAsString(book));
            return ResponseEntity.ok(messageSource
                    .getMessage("deleteImageSuccess", null, LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource
                    .getMessage("deleteImageError", null, LocaleContextHolder.getLocale()));
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

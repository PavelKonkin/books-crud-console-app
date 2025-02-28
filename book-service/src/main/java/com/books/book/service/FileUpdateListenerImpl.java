package com.books.book.service;

import com.books.dto.BookDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FileUpdateListenerImpl implements FileUpdateListener {
    private final BookService bookService;
    private final ObjectMapper objectMapper;

    public FileUpdateListenerImpl(BookService bookService, ObjectMapper objectMapper) {
        this.bookService = bookService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"new-file", "delete-file"})
    public void handleUpdatedFile(String bookJson) throws JsonProcessingException {
        BookDto bookDto = objectMapper.readValue(bookJson, BookDto.class);
        bookService.update(bookDto);
    }
}

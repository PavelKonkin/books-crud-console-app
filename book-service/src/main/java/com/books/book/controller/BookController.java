package com.books.book.controller;

import com.books.dto.BookDto;
import com.books.book.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;
    private  final MessageSource messageSource;

    public BookController(BookService bookService, MessageSource messageSource) {
        this.bookService = bookService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public List<BookDto> getAllBooks() {
        log.info(messageSource
                .getMessage("getAllBooksBeforeMessage", null, LocaleContextHolder.getLocale()));
        List<BookDto> books = bookService.getAllBooks();
        log.info(messageSource
                .getMessage("getAllBooksSuccessMessage", null, LocaleContextHolder.getLocale()), books);
        return books;
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable int id) {
        log.info(messageSource
                .getMessage("getBookBeforeMessage", null, LocaleContextHolder.getLocale()), id);
        BookDto book = bookService.getBook(id);
        log.info(messageSource
                .getMessage("getBookSuccessMessage", null, LocaleContextHolder.getLocale()), book);
        return book;
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id) {
        log.info(messageSource
                .getMessage("deleteBookBeforeMessage", null, LocaleContextHolder.getLocale()), id);
        bookService.delete(id);
        log.info(messageSource
                .getMessage("deleteBookSuccessMessage", null, LocaleContextHolder.getLocale()), id);
    }

    @PutMapping
    public void updateBook(@Valid @RequestBody BookDto bookDto) {
        log.info(messageSource
                .getMessage("updateBookBeforeMessage", null, LocaleContextHolder.getLocale()), bookDto);
        bookService.update(bookDto);
        log.info(messageSource
                .getMessage("updateBookSuccessMessage", null, LocaleContextHolder.getLocale()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Valid @RequestBody BookDto bookDto) {
        log.info("Получен запрос на создание книги с о свойствами {}", bookDto);
        log.info(messageSource
                .getMessage("createBookBeforeMessage", null, LocaleContextHolder.getLocale()), bookDto);
        BookDto savedBook = bookService.create(bookDto);
        log.info(messageSource
                .getMessage("createBookSuccessMessage", null, LocaleContextHolder.getLocale()), savedBook);
        return savedBook;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin_test")
    public ResponseEntity<String> adminAccess() {
        log.info(messageSource
                .getMessage("adminTestRequestReceived", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok("Admin content.");
    }
}

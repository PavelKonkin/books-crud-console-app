package com.books.book.controller;

import com.books.dto.BookDto;
import com.books.book.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> getAllBooks() {
        log.info("Request for list of all stored books has been received");
        List<BookDto> books = bookService.getAllBooks();
        log.info("List of all stored books has been received {}", books);
        return books;
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable int id) {
        log.info("Request book with id = {} has been received", id);
        BookDto book = bookService.getBook(id);
        log.info("Book was found {}", book);
        return book;
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id) {
        log.info("Request for deleting book with id = {} has been received", id);
        bookService.delete(id);
        log.info("Deleted book with id = {}", id);
    }

    @PutMapping
    public void updateBook(@Valid @RequestBody BookDto bookDto) {
        log.info("Request for updating book {} has been received", bookDto);
        bookService.update(bookDto);
        log.info("Book was updated");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Valid @RequestBody BookDto bookDto) {
        log.info("Request for creating book {} has been received", bookDto);
        BookDto savedBook = bookService.create(bookDto);
        log.info("Book {} was created", savedBook);
        return savedBook;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin_test")
    public ResponseEntity<String> adminAccess() {
        log.info("Request to admin only endpoint has been received");
        return ResponseEntity.ok("Admin content.");
    }
}

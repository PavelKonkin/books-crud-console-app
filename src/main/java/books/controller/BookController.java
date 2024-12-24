package books.controller;

import books.model.dto.BookDto;
import books.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public void createBook(@Valid @RequestBody BookDto bookDto) {
        log.info("Получен запрос на создание книги с о свойствами {}", bookDto);
        log.info(messageSource
                .getMessage("createBookBeforeMessage", null, LocaleContextHolder.getLocale()), bookDto);
        bookService.create(bookDto);
        log.info(messageSource
                .getMessage("createBookSuccessMessage", null, LocaleContextHolder.getLocale()));
        log.info("Книга создана");
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        log.info(messageSource
                .getMessage("uploadImageBeforeMessage", null, LocaleContextHolder.getLocale()), id);
        ResponseEntity<String> response = bookService.uploadImage(id, file);
        log.info(messageSource
                .getMessage("uploadImageSuccessMessage", null, LocaleContextHolder.getLocale()), id);
        return response;

    }

    @GetMapping("/{id}/image")
    public void downloadImage(@PathVariable int id, HttpServletResponse response) {
        log.info(messageSource
                .getMessage("downloadImageBeforeMessage", null, LocaleContextHolder.getLocale()), id);
        bookService.downloadImage(id, response);
        log.info(messageSource
                .getMessage("downloadImageSuccessMessage", null, LocaleContextHolder.getLocale()), id);
    }
}

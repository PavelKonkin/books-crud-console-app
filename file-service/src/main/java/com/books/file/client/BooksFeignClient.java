package com.books.file.client;

import com.books.dto.BookDto;
import com.books.exception.NotFoundException;
import com.books.file.config.BooksFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "book-service", configuration = BooksFeignConfig.class,
        fallbackFactory = BooksFeignClientFallback.class)
@Primary
public interface BooksFeignClient {
    @PutMapping("/api/v1/books")
    void updateBook(@RequestHeader("Authorization") String token, @RequestBody BookDto bookDto);

    @GetMapping("/api/v1/books/{id}")
    BookDto getBook(@RequestHeader("Authorization") String token, @PathVariable int id) throws NotFoundException;
}

package com.books.book.service;

import com.books.dto.BookDto;

import java.util.List;

public interface BookService {
    void create(BookDto bookDto);

    void update(BookDto bookDto);

    void delete(Integer id);

    List<BookDto> getAllBooks();

    BookDto getBook(int bookId);
}

package com.books.book.service;

import com.books.book.mapper.BookMapper;
import com.books.book.model.Book;
import com.books.book.repository.BookRepository;
import com.books.dto.BookDto;
import com.books.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final MessageSource messageSource;


    @Autowired
    public BookServiceImpl(BookMapper bookMapper, BookRepository bookRepository, MessageSource messageSource) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.messageSource = messageSource;
    }

    @Override
    public void create(BookDto bookDto) {
        Book book = bookMapper.convertBookDto(bookDto);
        bookRepository.save(book);
    }

    @Override
    public void update(BookDto bookDto) {
        bookRepository.save(bookMapper.convertBookDto(bookDto));
    }

    @Override
    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::convertBook)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto getBook(int bookId) {
        return bookMapper.convertBook(bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(messageSource
                        .getMessage("bookNotFound",null, LocaleContextHolder.getLocale()))));
    }
}

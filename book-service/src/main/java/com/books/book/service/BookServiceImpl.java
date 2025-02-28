package com.books.book.service;

import com.books.book.mapper.BookMapper;
import com.books.book.model.Book;
import com.books.book.repository.BookRepository;
import com.books.dto.BookDto;
import com.books.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final MessageSource messageSource;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_DELETE_BOOK = "delete-book";

    @Autowired
    public BookServiceImpl(BookMapper bookMapper, BookRepository bookRepository,
                           MessageSource messageSource, KafkaTemplate<String, String> kafkaTemplate) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
        this.messageSource = messageSource;
        this.kafkaTemplate = kafkaTemplate;
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
        Book bookToDelete = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(messageSource
                        .getMessage("bookNotFound",null, LocaleContextHolder.getLocale())));
        String fileId = bookToDelete.getImageId();
        bookRepository.delete(bookToDelete);
        if (!fileId.isEmpty()) {
            // Отправка сообщения в Kafka с id файла в MongoDB
            kafkaTemplate.send(TOPIC_DELETE_BOOK, bookToDelete.getImageId());
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::convertBook)
                .toList();
    }

    @Override
    public BookDto getBook(int bookId) {
        return bookMapper.convertBook(bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(messageSource
                        .getMessage("bookNotFound",null, LocaleContextHolder.getLocale()))));
    }
}

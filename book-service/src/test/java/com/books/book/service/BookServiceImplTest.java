package com.books.book.service;

import com.books.book.mapper.BookMapper;
import com.books.book.model.Book;
import com.books.book.repository.BookRepository;
import com.books.dto.BookDto;
import com.books.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private MessageSource messageSource;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @InjectMocks
    private BookServiceImpl bookService;

    private BookDto bookDto;
    private Book book;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setTitle("Test Book");

        book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        book.setImageId("test-image-id");
    }

    @Test
    void create_ShouldSaveBook() {
        when(bookMapper.convertBookDto(bookDto)).thenReturn(book);
        when(bookMapper.convertBook(book)).thenReturn(bookDto);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto savedBook = bookService.create(bookDto);

        verify(bookRepository, times(1)).save(book);
        assertThat(savedBook.getId(), equalTo(1));
        assertThat(savedBook.getTitle(), equalTo(bookDto.getTitle()));
    }

    @Test
    void update_ShouldUpdateBook() {
        when(bookMapper.convertBookDto(bookDto)).thenReturn(book);

        bookService.update(bookDto);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void delete_WhenBookExists_ShouldDeleteBookAndSendKafkaMessage() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        bookService.delete(1);

        verify(bookRepository, times(1)).delete(book);
        verify(kafkaTemplate, times(1)).send("delete-book", "test-image-id");
    }

    @Test
    void delete_WhenBookNotExists_ShouldThrowNotFoundException() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("bookNotFound"), isNull(), any()))
                .thenReturn("Book not found");

        assertThrows(NotFoundException.class, () -> bookService.delete(1));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void delete_WhenNoImageId_ShouldNotSendKafkaMessage() {
        Book bookWithoutImage = new Book();
        bookWithoutImage.setImageId("");
        when(bookRepository.findById(1)).thenReturn(Optional.of(bookWithoutImage));

        bookService.delete(1);

        verify(bookRepository, times(1)).delete(bookWithoutImage);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }


    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        List<Book> books = Collections.singletonList(book);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.convertBook(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.getAllBooks();

        verify(bookMapper, times(1)).convertBook(book);
        verify(bookRepository, times(1)).findAll();
        assertThat(1, equalTo(result.size()));
        assertThat(bookDto, equalTo(result.get(0)));
    }

    @Test
    void getAllBooks_whenThereAreNoBooks_ShouldReturnAllBooks() {
        List<Book> books = Collections.emptyList();
        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> result = bookService.getAllBooks();

        verify(bookMapper, never()).convertBook(any(Book.class));
        verify(bookRepository, times(1)).findAll();
        assertThat(0, equalTo(result.size()));
    }

    @Test
    void getBook_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookMapper.convertBook(book)).thenReturn(bookDto);

        BookDto result = bookService.getBook(1);

        assertThat(bookDto, equalTo(result));
        verify(bookMapper, times(1)).convertBook(book);
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void getBook_WhenBookNotExists_ShouldThrowNotFoundException() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("bookNotFound"), isNull(), any()))
                .thenReturn("Book not found");

        assertThrows(NotFoundException.class, () -> bookService.getBook(1));
        verify(bookMapper, never()).convertBook(any(Book.class));
        verify(bookRepository, times(1)).findById(1);
    }
}
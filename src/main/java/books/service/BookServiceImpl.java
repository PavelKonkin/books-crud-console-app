package books.service;

import books.mapper.BookMapper;
import books.model.Book;
import books.model.dto.BookDto;
import books.storage.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;


    @Autowired
    public BookServiceImpl(BookMapper bookMapper, BookRepository bookRepository) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public void create(BookDto bookDto) {
        Book book = bookMapper.convertBookDto(bookDto);
        bookRepository.save(book);
    }

    @Override
    public BookDto get(Integer id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(bookMapper::convertBook).orElse(null);
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
}

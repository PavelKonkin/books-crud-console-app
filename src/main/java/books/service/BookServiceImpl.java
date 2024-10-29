package books.service;

import books.mapper.BookMapper;
import books.model.Book;
import books.model.dto.BookDto;
import books.storage.BookStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookStorage bookStorage;


    @Autowired
    public BookServiceImpl(BookMapper bookMapper, BookStorage bookStorage) {
        this.bookMapper = bookMapper;
        this.bookStorage = bookStorage;
    }

    @Override
    public void create(BookDto bookDto) throws IOException {
        Book book = bookMapper.convertBookDto(bookDto);
        bookStorage.add(book);
    }

    @Override
    public BookDto get(Integer id) {
        Optional<Book> book = bookStorage.get(id);
        return book.map(bookMapper::convertBook).orElse(null);
    }

    @Override
    public void update(BookDto bookDto) throws IOException {
        bookStorage.update(bookMapper.convertBookDto(bookDto));
    }

    @Override
    public void delete(Integer id) throws IOException {
        bookStorage.delete(id);
    }

    @Override
    public List<BookDto> getAllBooks() throws IOException {
        return bookStorage.findAll().stream()
                .map(bookMapper::convertBook)
                .collect(Collectors.toList());
    }
}

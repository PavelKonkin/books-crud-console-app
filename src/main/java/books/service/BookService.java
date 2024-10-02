package books.service;

import books.model.dto.BookDto;

import java.io.IOException;
import java.util.List;

public interface BookService {
    void create(BookDto bookDto) throws IOException;

    BookDto get(Integer id);

    void update(BookDto bookDto) throws IOException;

    void delete(Integer id) throws IOException;

    List<BookDto> getAllBooks() throws IOException;
}

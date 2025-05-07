package books.service;

import books.model.dto.BookDto;

import java.util.List;

public interface BookService {
    void create(BookDto bookDto);

    BookDto get(Integer id);

    void update(BookDto bookDto);

    void delete(Integer id);

    List<BookDto> getAllBooks();
}

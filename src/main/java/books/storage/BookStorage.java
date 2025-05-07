package books.storage;

import books.model.Book;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookStorage {
    void add(Book book) throws IOException;

    Optional<Book> get(Integer id);

    void update(Book updatedBook) throws IOException;

    void delete(Integer id) throws IOException;

    List<Book> findAll() throws IOException;
}

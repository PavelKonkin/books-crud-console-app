package books.service;

import books.model.dto.BookDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    void create(BookDto bookDto);

    BookDto get(Integer id);

    void update(BookDto bookDto);

    void delete(Integer id);

    List<BookDto> getAllBooks();

    void downloadImage(int id, HttpServletResponse response);

    ResponseEntity<String> uploadImage(int id, MultipartFile file);
}

package books.mapper;

import books.model.Book;
import books.model.dto.BookDto;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public Book convertBookDto(BookDto bookDto) {
        return Book.builder()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .description(bookDto.getDescription())
                .build();
    }

    public BookDto convertBook(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .build();
    }
}

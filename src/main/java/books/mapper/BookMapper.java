package books.mapper;

import books.model.Book;
import books.model.dto.BookDto;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    private final AuthorMapper authorMapper;
    private final GenreMapper genreMapper;

    public BookMapper(AuthorMapper authorMapper, GenreMapper genreMapper) {
        this.authorMapper = authorMapper;
        this.genreMapper = genreMapper;
    }

    public Book convertBookDto(BookDto bookDto) {
        return Book.builder()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .authors(authorMapper.convertAuthorDtoList(bookDto.getAuthors()))
                .genres(genreMapper.convertGenreDtoList(bookDto.getGenres()))
                .description(bookDto.getDescription())
                .imageId(bookDto.getImageId())
                .build();
    }

    public BookDto convertBook(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authors(authorMapper.convertAuthorList(book.getAuthors()))
                .genres(genreMapper.convertGenreList(book.getGenres()))
                .description(book.getDescription())
                .imageId(book.getImageId())
                .build();
    }
}

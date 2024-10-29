package books.mapper;

import books.model.Author;
import books.model.dto.AuthorDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {
    public List<AuthorDto> convertAuthorList(List<Author> authors) {
        return authors.stream()
                .map(this::convertAuthorToDto)
                .collect(Collectors.toList());
    }

    public AuthorDto convertAuthorToDto(Author author) {
        return AuthorDto.builder()
                .name(author.getName())
                .build();
    }

    public List<Author> convertAuthorDtoList(List<AuthorDto> authors) {
        return authors.stream()
                .map(this::convertAuthorDtoToAuthor)
                .collect(Collectors.toList());
    }

    public Author convertAuthorDtoToAuthor(AuthorDto author) {
        return Author.builder()
                .name(author.getName())
                .build();
    }
}

package com.books.book.mapper;

import com.books.book.model.Author;
import com.books.dto.AuthorDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {
    public List<AuthorDto> convertAuthorList(Set<Author> authors) {
        return authors.stream()
                .map(this::convertAuthorToDto)
                .toList();
    }

    public AuthorDto convertAuthorToDto(Author author) {
        return AuthorDto.builder()
                .name(author.getName())
                .build();
    }

    public Set<Author> convertAuthorDtoList(List<AuthorDto> authors) {
        return authors.stream()
                .map(this::convertAuthorDtoToAuthor)
                .collect(Collectors.toSet());
    }

    public Author convertAuthorDtoToAuthor(AuthorDto author) {
        return Author.builder()
                .name(author.getName())
                .build();
    }
}

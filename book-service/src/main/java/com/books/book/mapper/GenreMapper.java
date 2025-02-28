package com.books.book.mapper;

import com.books.book.model.Genre;
import com.books.dto.GenreDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreMapper {
    public List<GenreDto> convertGenreList(Set<Genre> genres) {
        return genres.stream()
                .map(this::convertGenreToDto)
                .toList();
    }

    public GenreDto convertGenreToDto(Genre genre) {
        return GenreDto.builder()
                .title(genre.getTitle())
                .build();
    }

    public Set<Genre> convertGenreDtoList(List<GenreDto> genres) {
        return genres.stream()
                .map(this::convertGenreDtoToGenre)
                .collect(Collectors.toSet());
    }

    public Genre convertGenreDtoToGenre(GenreDto genreDto) {
        return Genre.builder()
                .title(genreDto.getTitle())
                .build();
    }
}

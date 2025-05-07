package books.mapper;

import books.model.Genre;
import books.model.dto.GenreDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreMapper {
    public List<GenreDto> convertGenreList(List<Genre> genres) {
        return genres.stream()
                .map(this::convertGenreToDto)
                .collect(Collectors.toList());
    }

    public GenreDto convertGenreToDto(Genre genre) {
        return GenreDto.builder()
                .title(genre.getTitle())
                .build();
    }

    public List<Genre> convertGenreDtoList(List<GenreDto> genres) {
        return genres.stream()
                .map(this::convertGenreDtoToGenre)
                .collect(Collectors.toList());
    }

    public Genre convertGenreDtoToGenre(GenreDto genreDto) {
        return Genre.builder()
                .title(genreDto.getTitle())
                .build();
    }
}

package books.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class BookDto {
    private Integer id;
    private String title;
    private List<AuthorDto> authors;
    private List<GenreDto> genres;
    private String description;

    @Override
    public String toString() {
        return "BookDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", genres=" + genres +
                ", description='" + description + '\'' +
                '}';
    }
}

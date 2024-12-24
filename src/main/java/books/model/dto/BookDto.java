package books.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class BookDto {
    private Integer id;
    @NotBlank
    @Size(min = 2, max = 100)
    private String title;
    @NotEmpty
    @Valid
    private List<AuthorDto> authors;
    @NotEmpty
    @Valid
    private List<GenreDto> genres;
    @NotBlank
    @Size(min = 2, max = 1000)
    private String description;
    private String imageId;

    @Override
    public String toString() {
        return "BookDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", genres=" + genres +
                ", imageId=" + imageId +
                ", description='" + description + '\'' +
                '}';
    }
}

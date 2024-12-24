package books.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class GenreDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String title;

    @Override
    public String toString() {
        return "GenreDto{" +
                "title='" + title + '\'' +
                '}';
    }
}

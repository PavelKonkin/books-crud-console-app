package books.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class GenreDto {
    private String title;

    @Override
    public String toString() {
        return "GenreDto{" +
                "title='" + title + '\'' +
                '}';
    }
}

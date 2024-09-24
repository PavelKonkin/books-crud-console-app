package books.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class BookDto {
    private Integer id;
    private String title;
    private String author;
    private String description;
}

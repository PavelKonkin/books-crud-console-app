package books.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "title", "author", "description"})
public class Book {
    private Integer id;
    private String title;
    private String author;
    private String description;
}

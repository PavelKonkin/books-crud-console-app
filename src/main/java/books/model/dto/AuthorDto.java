package books.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class AuthorDto {
    private String name;

    @Override
    public String toString() {
        return "AuthorDto{" +
                "name='" + name + '\'' +
                '}';
    }
}

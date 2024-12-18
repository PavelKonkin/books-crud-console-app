package books.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class AuthorDto {
    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @Override
    public String toString() {
        return "AuthorDto{" +
                "name='" + name + '\'' +
                '}';
    }
}

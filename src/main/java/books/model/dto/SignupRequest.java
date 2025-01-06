package books.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {
    @NotBlank
    @Size(min = 2, max = 255)
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    private String password;

    public SignupRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

package com.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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

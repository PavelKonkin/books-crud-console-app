package com.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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

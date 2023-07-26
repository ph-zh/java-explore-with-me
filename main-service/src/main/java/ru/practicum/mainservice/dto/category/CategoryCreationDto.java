package ru.practicum.mainservice.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryCreationDto {
    @Size(max = 255)
    @NotBlank
    private String name;
}

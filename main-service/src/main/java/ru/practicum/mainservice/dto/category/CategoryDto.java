package ru.practicum.mainservice.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private int id;
    private String name;
}

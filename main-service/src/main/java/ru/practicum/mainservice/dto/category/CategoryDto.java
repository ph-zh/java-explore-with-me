package ru.practicum.mainservice.dto.category;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class CategoryDto {
    private int id;
    @Length(min = 2, max = 50)
    private String name;
}

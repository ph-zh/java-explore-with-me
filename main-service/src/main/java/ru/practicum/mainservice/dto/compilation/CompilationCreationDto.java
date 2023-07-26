package ru.practicum.mainservice.dto.compilation;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
public class CompilationCreationDto {
    private Set<Long> events;
    private boolean pinned;
    @NotBlank
    @Size(min = 5, max = 255)
    private String title;
}

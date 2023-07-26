package ru.practicum.mainservice.dto.compilation;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.dto.event.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
public class CompilationDto {
    @Positive
    private int id;
    private List<EventShortDto> events;
    private boolean pinned;
    @NotBlank
    private String title;
}

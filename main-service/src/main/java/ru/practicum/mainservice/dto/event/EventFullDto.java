package ru.practicum.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.dto.user.UserShortDto;
import ru.practicum.mainservice.model.EventState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class EventFullDto {
    @Positive
    private long id;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime createdOn;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    @NotNull
    private EventState state;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    private long views;
}

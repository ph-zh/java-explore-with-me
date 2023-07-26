package ru.practicum.mainservice.dto.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserShortDto {
    @Positive
    private long id;
    @NotBlank
    private String name;
}

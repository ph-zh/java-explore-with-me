package ru.practicum.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatResponseDto {
    private String app;
    private String uri;
    private long hits;
}

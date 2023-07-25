package ru.practicum.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatHits {
    private String app;
    private String uri;
    private long hits;
}

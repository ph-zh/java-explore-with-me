package ru.practicum.server.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class EndpointHit {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}

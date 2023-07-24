package ru.practicum.server.service;

import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(EndpointHitRequestDto requestDto);

    List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

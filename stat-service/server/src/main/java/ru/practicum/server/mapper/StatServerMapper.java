package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.StatHits;

@UtilityClass
public class StatServerMapper {

    public static EndpointHit toEndpointHit(EndpointHitRequestDto requestDto) {
        return EndpointHit.builder()
                .app(requestDto.getApp())
                .uri(requestDto.getUri())
                .ip(requestDto.getIp())
                .timestamp(requestDto.getTimestamp())
                .build();
    }

    public static StatResponseDto toStatResponseDto(StatHits statHits) {
        return new StatResponseDto(statHits.getApp(), statHits.getUri(), statHits.getHits());
    }
}

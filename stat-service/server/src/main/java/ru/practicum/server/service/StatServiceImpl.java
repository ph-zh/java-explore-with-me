package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.server.mapper.StatServerMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.StatHits;
import ru.practicum.server.repository.StatServerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatServiceImpl implements StatService {
    private final StatServerRepository statServerRepository;

    @Override
    public void addHit(EndpointHitRequestDto requestDto) {
        EndpointHit endpointHit = StatServerMapper.toEndpointHit(requestDto);
        statServerRepository.save(endpointHit);
    }

    @Override
    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<StatHits> stats;

        if (uris.isEmpty()) {
            stats = statServerRepository.findAllStats(start, end, unique);
        } else {
            stats = statServerRepository.findAllStatsWithUris(uris, start, end, unique);
        }

        return stats.stream().map(StatServerMapper::toStatResponseDto).collect(Collectors.toList());
    }
}

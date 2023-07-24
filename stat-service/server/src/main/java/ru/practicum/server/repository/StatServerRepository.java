package ru.practicum.server.repository;

import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.StatHits;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerRepository {

    void save(EndpointHit endpointHit);

    List<StatHits> findAllStatsWithUris(List<String> uris, LocalDateTime start, LocalDateTime end, boolean unique);

    List<StatHits> findAllStats(LocalDateTime start, LocalDateTime end, boolean unique);
}

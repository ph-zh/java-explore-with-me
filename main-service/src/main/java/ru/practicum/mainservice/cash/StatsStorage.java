package ru.practicum.mainservice.cash;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatClient;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.mainservice.mapper.DateTimeMapper;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@Component
@Slf4j
public class StatsStorage {
    private Map<Long, Long> hits = new HashMap<>();
    private final EventRepository eventRepository;
    private final StatClient statClient;

    @Scheduled(fixedDelayString = "PT30S")
    public void updateHitsForPublishedEvents() {
        List<Long> ids = eventRepository.findAllEventIdsWithPublishedState(EventState.PUBLISHED);

        List<String> uris = ids.stream().map(id -> String.format("/events/%d", id)).collect(Collectors.toList());

        List<StatResponseDto> stats;

        if (hits.isEmpty()) {
            stats = statClient.getStats(DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now().minusYears(10)),
                    DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now().plusYears(10)), uris, false);
        } else {
            LocalDateTime timestamp = LocalDateTime.now().minusSeconds(30);

            stats = statClient.getStats(DateTimeMapper.fromLocalDateTimeToString(timestamp),
                    DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now().plusYears(10)), uris, false);
        }

        for (StatResponseDto stat : stats) {
            Long id = Long.valueOf(stat.getUri().substring(8));
            hits.put(id, stat.getHits());
        }
        log.info("Cash of stats is updated");
    }
}

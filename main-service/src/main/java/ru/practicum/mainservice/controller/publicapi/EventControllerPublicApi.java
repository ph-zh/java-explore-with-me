package ru.practicum.mainservice.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatClient;
import ru.practicum.dto.EndpointHitRequestDto;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class EventControllerPublicApi {
    private final EventService eventService;
    private final StatClient statClient;

    @Value("${service-name}")
    private String serviceName;

    @GetMapping("/events")
    public List<EventShortDto> getEventsPublicApi(@RequestParam(defaultValue = "") String text,
                                                  @RequestParam(defaultValue = "") List<Integer> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) boolean onlyAvailable,
                                                  @RequestParam(defaultValue = "") String sort,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  HttpServletRequest request) {
        sendRequestToStatService(request);

        return eventService.getEventsPublicApi(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable @Positive long eventId,
                                     HttpServletRequest request) {
        sendRequestToStatService(request);

        return eventService.getEventById(eventId);
    }

    private void sendRequestToStatService(HttpServletRequest request) {
        EndpointHitRequestDto endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app(serviceName)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        statClient.addHit(endpointHitRequestDto);
    }
}

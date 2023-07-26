package ru.practicum.mainservice.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.UpdateEventRequestDto;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class EventControllerAdminApi {
    private final EventService eventService;

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByEventId(@PathVariable @Positive long eventId,
                                             @RequestBody @Valid UpdateEventRequestDto updateEventRequestDto) {
        return eventService.updateEventByEventId(eventId, updateEventRequestDto);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEventsAdminApi(@RequestParam(defaultValue = "") List<Long> users,
                                                @RequestParam(defaultValue = "") List<EventState> states,
                                                @RequestParam(defaultValue = "") List<Integer> categories,
                                                @RequestParam(required = false) LocalDateTime rangeStart,
                                                @RequestParam(required = false) LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getEventsAdminApi(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}

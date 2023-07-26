package ru.practicum.mainservice.controller.userapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventCreationDto;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.UpdateEventRequestDto;
import ru.practicum.mainservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class EventControllerUserApi {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @Positive long userId,
                                 @RequestBody @Valid EventCreationDto eventCreationDto) {
        return eventService.addEvent(userId, eventCreationDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllUserEvents(@PathVariable @Positive long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getAllUserEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserAndEventId(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive long eventId) {
        return eventService.getEventByUserAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUserIdAndEventId(@PathVariable @Positive long userId,
                                                      @PathVariable @Positive long eventId,
                                                      @RequestBody @Valid UpdateEventRequestDto updateEventRequestDto) {
        return eventService.updateEventByUserIdAndEventId(userId, eventId, updateEventRequestDto);
    }
}

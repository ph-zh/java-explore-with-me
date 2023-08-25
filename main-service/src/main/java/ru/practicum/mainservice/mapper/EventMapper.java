package ru.practicum.mainservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.dto.event.EventCreationDto;
import ru.practicum.mainservice.dto.event.EventFullDto;
import ru.practicum.mainservice.dto.event.EventShortDto;
import ru.practicum.mainservice.dto.event.UpdateEventRequestDto;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.StateAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public static Event toEvent(EventCreationDto eventCreationDto) {
        return Event.builder()
                .annotation(eventCreationDto.getAnnotation())
                .description(eventCreationDto.getDescription())
                .lon(eventCreationDto.getLocation().getLon())
                .lat(eventCreationDto.getLocation().getLat())
                .paid(eventCreationDto.isPaid())
                .participantLimit(eventCreationDto.getParticipantLimit())
                .requestModeration(eventCreationDto.isRequestModeration())
                .title(eventCreationDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationDto.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequest())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequest())
                .build();
    }

    public static Event fromUpdateDtoToEvent(UpdateEventRequestDto updateDto, Event event,
                                             Category category, LocalDateTime eventDate, StateAction stateAction) {
        event.setAnnotation((updateDto.getAnnotation() == null || updateDto.getAnnotation().isBlank())
                ? event.getAnnotation() : updateDto.getAnnotation());
        event.setCategory(category == null ? event.getCategory() : category);
        event.setDescription((updateDto.getDescription() == null || updateDto.getDescription().isBlank())
                ? event.getDescription() : updateDto.getDescription());
        event.setEventDate(eventDate == null ? event.getEventDate() : eventDate);
        event.setLat(updateDto.getLocation() == null ? event.getLat() : updateDto.getLocation().getLat());
        event.setLon(updateDto.getLocation() == null ? event.getLon() : updateDto.getLocation().getLon());
        event.setPaid(updateDto.getPaid() == null ? event.isPaid() : updateDto.getPaid());
        event.setParticipantLimit(updateDto.getParticipantLimit() == null ? event.getParticipantLimit() : updateDto.getParticipantLimit());
        event.setRequestModeration(updateDto.getRequestModeration() == null ? event.isRequestModeration() : updateDto.getRequestModeration());
        event.setTitle((updateDto.getTitle() == null || updateDto.getTitle().isBlank()) ? event.getTitle() : updateDto.getTitle());

        if (stateAction != null) {
            if (stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
            } else {
                event.setState(EventState.CANCELED);
            }
        }

        return event;
    }

    public static List<EventFullDto> toListOfEventFullDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public static List<EventShortDto> toListOfEventShortDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }
}
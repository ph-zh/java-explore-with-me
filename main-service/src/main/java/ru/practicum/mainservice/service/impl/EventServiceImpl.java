package ru.practicum.mainservice.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.client.StatClient;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.mainservice.cash.StatsStorage;
import ru.practicum.mainservice.constants.Constants;
import ru.practicum.mainservice.dto.event.*;
import ru.practicum.mainservice.exception.BadRequestException;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.mapper.DateTimeMapper;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.model.*;
import ru.practicum.mainservice.model.QEvent;
import ru.practicum.mainservice.pagination.OffsetBasedPageRequest;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.valid.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final Validator validator;
    private final StatClient statClient;
    private final RequestRepository requestRepository;
    private final StatsStorage statsStorage;

    @Transactional
    @Override
    public @Valid EventFullDto updateEventByEventId(long eventId, UpdateEventRequestDto updateEventRequestDto) {
        LocalDateTime eventDate = null;

        if (updateEventRequestDto.getEventDate() != null) {
            eventDate = updateEventRequestDto.getEventDate();
            Validator.throwIfEventDateIsNotLaterOneHourAfterNow(eventDate);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        StateAction state = null;
        String stateAction = updateEventRequestDto.getStateAction();

        if (stateAction != null) {
            if (!StateAction.PUBLISH_EVENT.toString().equals(stateAction) && !StateAction.REJECT_EVENT.toString().equals(stateAction)) {
                throw new ConflictException("Field StateAction is incorrect");
            }
            state = StateAction.valueOf(stateAction);

            if (!event.getState().equals(EventState.PENDING) && state.equals(StateAction.PUBLISH_EVENT)) {
                throw new ConflictException("Event must be PENDING state to be published");
            }
            if (event.getState().equals(EventState.PUBLISHED) && state.equals(StateAction.REJECT_EVENT)) {
                throw new ConflictException("Event cannot be canceled if already published");
            }
        }

        Category category = null;
        if (updateEventRequestDto.getCategory() != null) {
            int idCat = updateEventRequestDto.getCategory();
            category = categoryRepository.findById(idCat)
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", idCat)));
        }

        EventMapper.fromUpdateDtoToEvent(updateEventRequestDto, event, category, eventDate, state);

        addViewsAndConfirmedRequestsForEvents(List.of(event));

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsAdminApi(List<Long> users, List<EventState> states, List<Integer> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        List<Event> events;
        Pageable pageable = new OffsetBasedPageRequest(from, size, Constants.SORT_BY_ID_DESC);
        BooleanBuilder where = new BooleanBuilder();

        if (!users.isEmpty()) {
            BooleanExpression byUsersId = QEvent.event.initiator.id.in(users);
            where.and(byUsersId);
        }
        if (!states.isEmpty()) {
            BooleanExpression byStates = QEvent.event.state.in(states);
            where.and(byStates);
        }
        if (!categories.isEmpty()) {
            BooleanExpression byCategory = QEvent.event.category.id.in(categories);
            where.and(byCategory);
        }

        BooleanExpression byEventDate;
        if (rangeStart == null && rangeEnd == null) {
            events = eventRepository.findAll(where, pageable).getContent();
            return EventMapper.toListOfEventFullDto(events);
        } else if (rangeStart == null) {
            byEventDate = QEvent.event.eventDate.before(rangeEnd);
        } else if (rangeEnd == null) {
            byEventDate = QEvent.event.eventDate.after(rangeStart);
        } else {
            byEventDate = QEvent.event.eventDate.between(rangeStart, rangeEnd);
        }
        where.and(byEventDate);
        events = eventRepository.findAll(where, pageable).getContent();

        addViewsAndConfirmedRequestsForEvents(events);

        return EventMapper.toListOfEventFullDto(events);
    }

    @Transactional
    @Override
    public @Valid EventFullDto addEvent(long userId, EventCreationDto eventCreationDto) {
        LocalDateTime eventDate = eventCreationDto.getEventDate();
        Validator.throwIfEventDateIsNotLaterTwoHoursAfterNow(eventDate);

        User user = validator.throwIfUserNotFoundOrReturnIfExist(userId);

        int categoryId = eventCreationDto.getCategory();
        Category category = validator.throwIfCategoryNotFoundOrReturnIfExist(categoryId);

        Event event = EventMapper.toEvent(eventCreationDto);
        event.setEventDate(eventDate);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);

        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllUserEvents(long userId, int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size, Constants.SORT_BY_ID_DESC);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

        addViewsAndConfirmedRequestsForEvents(events);

        return EventMapper.toListOfEventShortDto(events);
    }

    @Override
    public EventFullDto getEventByUserAndEventId(long userId, long eventId) {
        Event event = validator.throwIfEventFromCorrectUserNotFoundOrReturnIfExist(eventId, userId);

        addViewsAndConfirmedRequestsForEvents(List.of(event));

        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public @Valid EventFullDto updateEventByUserIdAndEventId(long userId, long eventId, UpdateEventRequestDto updateEventRequestDto) {
        LocalDateTime eventDate = null;
        if (updateEventRequestDto.getEventDate() != null) {
            eventDate = updateEventRequestDto.getEventDate();
            Validator.throwIfEventDateIsNotLaterTwoHoursAfterNow(eventDate);
        }

        String stateAction = updateEventRequestDto.getStateAction();
        if (!StateAction.SEND_TO_REVIEW.toString().equals(stateAction) && !StateAction.CANCEL_REVIEW.toString().equals(stateAction)) {
            throw new ConflictException("Field StateAction is incorrect");
        }

        StateAction state = StateAction.valueOf(stateAction);

        Category category = null;
        if (updateEventRequestDto.getCategory() != null) {
            category = validator.throwIfCategoryNotFoundOrReturnIfExist(updateEventRequestDto.getCategory());
        }

        Event event = validator.throwIfEventFromCorrectUserNotFoundOrReturnIfExist(eventId, userId);

        EventMapper.fromUpdateDtoToEvent(updateEventRequestDto, event, category, eventDate, state);

        addViewsAndConfirmedRequestsForEvents(List.of(event));

        return EventMapper.toEventFullDto(event);
    }

    @Transient
    @Override
    public List<EventShortDto> getEventsPublicApi(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from,
                                                  int size, HttpServletRequest request) {
        List<Event> events;
        Sort sortOption = Constants.SORT_BY_ID_DESC;
        if (sort.equals(EventSortOption.EVENT_DATE.toString())) {
            sortOption = Sort.by(Sort.Direction.ASC, "eventDate");
        } else if (sort.equals(EventSortOption.VIEWS.toString())) {
            sortOption = Sort.by(Sort.Direction.DESC, "views");
        }
        Pageable pageable = new OffsetBasedPageRequest(from, size, sortOption);

        BooleanBuilder where = new BooleanBuilder();

        BooleanExpression byPublishState = QEvent.event.state.eq(EventState.PUBLISHED);
        where.and(byPublishState);

        if (!text.isBlank()) {
            BooleanExpression byText = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
            where.and(byText);
        }
        if (!categories.isEmpty()) {
            BooleanExpression byCategories = QEvent.event.category.id.in(categories);
            where.and(byCategories);
        }
        if (paid != null) {
            BooleanExpression byPaid = QEvent.event.paid.eq(paid);
            where.and(byPaid);
        }

        BooleanExpression byEventDate;
        if (rangeStart == null && rangeEnd == null) {
            LocalDateTime now = LocalDateTime.now();
            byEventDate = QEvent.event.eventDate.after(now);
        } else if (rangeStart == null) {
            byEventDate = QEvent.event.eventDate.before(rangeEnd);
        } else if (rangeEnd == null) {
            byEventDate = QEvent.event.eventDate.after(rangeStart);
        } else {
            byEventDate = QEvent.event.eventDate.between(rangeStart, rangeEnd);
        }
        where.and(byEventDate);

        events = eventRepository.findAll(where, pageable).getContent();
        addViewsAndConfirmedRequestsForEvents(events);

        if (onlyAvailable) {
            events = events.stream().filter(ev -> ev.getConfirmedRequest() != ev.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        return EventMapper.toListOfEventShortDto(events);
    }

    @Override
    @Transient
    public EventFullDto getEventById(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event is not available because it has not been published yet");
        }

        addViewsAndConfirmedRequestsForEvents(List.of(event));

        return EventMapper.toEventFullDto(event);
    }

    private Map<Long, Long> getHits(List<Long> ids) {
        Map<Long, Long> result = new HashMap<>();

        Map<Long, Long> hitsFromCash = statsStorage.getHits();
        List<Long> idOfEventWhichNotCashed = new ArrayList<>();

        for (Long id : ids) {
            if (!hitsFromCash.containsKey(id)) {
                idOfEventWhichNotCashed.add(id);
            } else {
                result.put(id, hitsFromCash.get(id));
            }
        }

        if (!idOfEventWhichNotCashed.isEmpty()) {
            List<String> uris = idOfEventWhichNotCashed.stream().map(id -> String.format("/events/%d", id))
                    .collect(Collectors.toList());
            List<StatResponseDto> stats = statClient
                    .getStats(DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now().minusYears(10)),
                            DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now().plusYears(10)), uris, false);

            for (StatResponseDto stat : stats) {
                Long id = Long.valueOf(stat.getUri().substring(8));
                result.put(id, stat.getHits());
                statsStorage.getHits().put(id, stat.getHits());
            }
        }

        return result;
    }

    private void addViewsAndConfirmedRequestsForEvents(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> hits = getHits(ids);

        for (Event ev : events) {
            ev.setViews(hits.getOrDefault(ev.getId(), 0L));
        }

        Map<Long, Integer> confirmedRequests = requestRepository.findAllConfirmedRequestsByEventIds(ids, RequestStatus.CONFIRMED);

        for (Event ev : events) {
            ev.setConfirmedRequest(confirmedRequests.getOrDefault(ev.getId(), 0));
        }
    }
}
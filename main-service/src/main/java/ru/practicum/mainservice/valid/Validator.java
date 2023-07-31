package ru.practicum.mainservice.valid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.exception.BadRequestException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class Validator {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public static void throwIfEventDateIsNotLaterOneHourAfterNow(LocalDateTime eventDate) {
        LocalDateTime timestamp = LocalDateTime.now().plusHours(1);
        if (eventDate.isBefore(timestamp)) {
            throw new BadRequestException("Event cannot start earlier than 1 hours from now");
        }
    }

    public static void throwIfEventDateIsNotLaterTwoHoursAfterNow(LocalDateTime eventDate) {
        LocalDateTime timestamp = LocalDateTime.now().plusHours(2);
        if (eventDate.isBefore(timestamp)) {
            throw new BadRequestException("Event cannot start earlier than 2 hours from now");
        }
    }

    public User throwIfUserNotFoundOrReturnIfExist(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
    }

    public Category throwIfCategoryNotFoundOrReturnIfExist(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", categoryId)));
    }

    public Event throwIfEventFromCorrectUserNotFoundOrReturnIfExist(long eventId, long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    public Event throwIfEventNotFoundOrReturnIfExist(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }
}

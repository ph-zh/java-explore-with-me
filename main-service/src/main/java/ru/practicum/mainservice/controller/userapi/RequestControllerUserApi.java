package ru.practicum.mainservice.controller.userapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateResultDto;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class RequestControllerUserApi {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable @Positive long userId,
                                    @RequestParam @Positive long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getUserRequests(@PathVariable @Positive long userId) {
        return requestService.getUserRequests(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive long userId,
                                    @PathVariable @Positive long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive long eventId) {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateResultDto updateRequestsStatus(@PathVariable @Positive long userId,
                                                             @PathVariable @Positive long eventId,
                                                             @RequestBody @Valid RequestStatusUpdateRequestDto updateDto) {
        return requestService.updateRequestsStatus(userId, eventId, updateDto);
    }
}

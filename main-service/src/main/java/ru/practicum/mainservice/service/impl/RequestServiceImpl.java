package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateRequestDto;
import ru.practicum.mainservice.dto.request.RequestStatusUpdateResultDto;
import ru.practicum.mainservice.dto.request.StatusOfUpdateRequest;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.mapper.RequestMapper;
import ru.practicum.mainservice.model.*;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.service.RequestService;
import ru.practicum.mainservice.valid.Validator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final Validator validator;

    @Transactional
    @Override
    public RequestDto createRequest(long userId, long eventId) {
        Event event = validator.throwIfEventNotFoundOrReturnIfExist(eventId);
        User user = validator.throwIfUserNotFoundOrReturnIfExist(userId);
        Request request;

        if (userId == event.getInitiator().getId()) {
            throw new ConflictException("Initiator of event cannot be requester");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Сan't participate in an unpublished event");
        }
        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() == getCountOfConfirmedRequestsByEventId(eventId)) {
            throw new ConflictException("Participation limit expired");
        }

        if (!event.isRequestModeration()) {
            request = Request.builder()
                    .event(event)
                    .requester(user)
                    .status(RequestStatus.CONFIRMED)
                    .build();
        } else {
            request = Request.builder()
                    .event(event)
                    .requester(user)
                    .status(RequestStatus.PENDING)
                    .build();
        }

        requestRepository.save(request);

        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequestsByEventId(long userId, long eventId) {
        validator.throwIfEventFromCorrectUserNotFoundOrReturnIfExist(eventId, userId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return RequestMapper.toListOfRequestDto(requests);
    }

    @Override
    public List<RequestDto> getUserRequests(long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return RequestMapper.toListOfRequestDto(requests);
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));

        if (request.getRequester().getId() != userId) {
            throw new ConflictException(String.format("User with id: %d is not the requester" +
                    " of the event and cannot cancel the request", userId));
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(request);
    }

    @Transactional
    @Override
    public @Valid RequestStatusUpdateResultDto updateRequestsStatus(long userId, long eventId,
                                                                    RequestStatusUpdateRequestDto updateDto) {
        Event event = validator.throwIfEventFromCorrectUserNotFoundOrReturnIfExist(eventId, userId);
        List<Request> requests = requestRepository.findAllByIdIn(updateDto.getRequestIds());

        RequestStatusUpdateResultDto resultDto = RequestStatusUpdateResultDto.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            resultDto.getConfirmedRequests().addAll(RequestMapper.toListOfRequestDto(requests));
            return resultDto;
        }

        int confirmedRequests = getCountOfConfirmedRequestsByEventId(eventId);
        if (event.getParticipantLimit() == confirmedRequests) {
            throw new ConflictException("Limit of requests for participation is over");
        }

        if (!requests.stream().allMatch(r -> r.getStatus().equals(RequestStatus.PENDING))) {
            throw new ConflictException("All requests should be in status PENDING");
        }

        if (updateDto.getStatus().equals(StatusOfUpdateRequest.REJECTED)) {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            resultDto.getRejectedRequests().addAll(RequestMapper.toListOfRequestDto(requests));
            return resultDto;
        }

        int reserve = event.getParticipantLimit() - confirmedRequests;

        for (Request request : requests) {
            if (reserve > 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                resultDto.getConfirmedRequests().add(RequestMapper.toRequestDto(request));
                --reserve;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                resultDto.getRejectedRequests().add(RequestMapper.toRequestDto(request));
            }
        }
        return resultDto;
    }

    private int getCountOfConfirmedRequestsByEventId(long eventId) {
        return requestRepository.countAllByEventIdAndStatusEquals(eventId,
                RequestStatus.CONFIRMED);
    }
}

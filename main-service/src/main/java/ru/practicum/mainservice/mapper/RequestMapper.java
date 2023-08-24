package ru.practicum.mainservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public static List<RequestDto> toListOfRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }
}

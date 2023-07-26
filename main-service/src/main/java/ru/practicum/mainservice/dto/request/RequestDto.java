package ru.practicum.mainservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestDto {
    private long id;
    private long event;
    private long requester;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private RequestStatus status;
}

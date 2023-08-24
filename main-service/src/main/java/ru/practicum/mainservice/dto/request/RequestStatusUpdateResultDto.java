package ru.practicum.mainservice.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class RequestStatusUpdateResultDto {
    @NotNull
    private List<RequestDto> confirmedRequests;
    @NotNull
    private List<RequestDto> rejectedRequests;
}

package ru.practicum.mainservice.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDto {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}

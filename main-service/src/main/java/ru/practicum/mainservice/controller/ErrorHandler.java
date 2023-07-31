package ru.practicum.mainservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.mainservice.dto.error.ErrorResponseDto;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.mapper.DateTimeMapper;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponseDto> notFoundExceptionHandler(RuntimeException e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status("NOT_FOUND")
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class})
    public ResponseEntity<ErrorResponseDto> conflictExceptionHandler(Exception e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status("CONFLICT")
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now()))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDto> badRequestExceptionHandler(Exception e) {
        log.error(e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(DateTimeMapper.fromLocalDateTimeToString(LocalDateTime.now()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }
}

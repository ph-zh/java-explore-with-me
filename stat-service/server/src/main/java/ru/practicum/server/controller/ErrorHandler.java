package ru.practicum.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidParameterException(final InvalidParameterException exception) {
        log.warn("Error! InvalidParameterException, server status: '{}' text message: '{}'",
                HttpStatus.BAD_REQUEST, exception.getMessage());
        return Map.of("End time no be after start time", exception.getMessage());
    }
}
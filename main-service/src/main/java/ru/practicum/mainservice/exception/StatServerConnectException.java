package ru.practicum.mainservice.exception;

public class StatServerConnectException extends RuntimeException {
    public StatServerConnectException(String message) {
        super(message);
    }
}

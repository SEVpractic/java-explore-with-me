package ru.practicum.ewmservice.util.exceptions;

public class OperationFailedException extends RuntimeException{
    public OperationFailedException(String message) {
        super(message);
    }
}

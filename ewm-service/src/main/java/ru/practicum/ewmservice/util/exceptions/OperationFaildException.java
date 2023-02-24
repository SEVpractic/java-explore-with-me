package ru.practicum.ewmservice.util.exceptions;

public class OperationFaildException extends RuntimeException{
    public OperationFaildException(String message) {
        super(message);
    }
}

package ru.practicum.ewmservice.util.exceptions;

public class EntityNotExistException extends RuntimeException {
    public EntityNotExistException(String message) {
        super(message);
    }
}

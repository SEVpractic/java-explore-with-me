package ru.practicum.ewmservice.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.ewmservice.util.exceptions.EntityNotExistException;
import ru.practicum.ewmservice.util.exceptions.OperationFailedException;
import ru.practicum.ewmservice.util.exceptions.EventDateValidationException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleThrowableException(Throwable ex) {
        log.info("500 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now()
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleConversionFailedException(ConversionFailedException ex) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info("400 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handlePSQLException(DataIntegrityViolationException ex) {
        log.info("409 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getCause().getMessage(),
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now()
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleEntityNotExistException(EntityNotExistException ex) {
        log.info("404 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleOperationFailedException(OperationFailedException ex) {
        log.info("409 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now()
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<Object> handleTimeValidationException(EventDateValidationException ex) {
        log.info("409 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now()
        ), HttpStatus.CONFLICT);
    }

    @Getter
    @RequiredArgsConstructor
    private static class ExceptionDto {
        private final String message; // Сообщение об ошибке
        private final String reason; // Общее описание причины ошибки
        private final String status; // Код статуса HTTP-ответа
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("timestamp")
        private final LocalDateTime timestamp; // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
    }
}

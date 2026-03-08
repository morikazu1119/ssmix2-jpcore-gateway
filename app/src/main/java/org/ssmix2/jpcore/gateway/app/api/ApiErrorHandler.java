package org.ssmix2.jpcore.gateway.app.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ssmix2.jpcore.gateway.core.parser.UnsupportedSsmix2InputException;

import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(UnsupportedSsmix2InputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleUnsupportedInput(UnsupportedSsmix2InputException exception) {
        return Map.of("error", "UNSUPPORTED_INPUT", "message", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationFailure(MethodArgumentNotValidException exception) {
        return Map.of("error", "BAD_REQUEST", "message", exception.getMessage());
    }
}


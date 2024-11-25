package com.willamsc.personalwebsite.common.handler;

import com.willamsc.personalwebsite.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        logger.error("Unexpected error", e);
        return Result.error("An unexpected error occurred");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "Access denied");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Result<String> handleBadCredentialsException(BadCredentialsException e) {
        return Result.error(401, "Invalid credentials");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<String> handleValidationExceptions(Exception e) {
        List<FieldError> fieldErrors;
        if (e instanceof MethodArgumentNotValidException) {
            fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        } else {
            fieldErrors = ((BindException) e).getBindingResult().getFieldErrors();
        }

        String message = fieldErrors.stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return Result.error(400, message);
    }
}

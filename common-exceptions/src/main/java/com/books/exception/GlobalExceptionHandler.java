package com.books.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    private static final String BAD_REQUEST = "badRequest";

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(BindException ex) {
        log.info("Validation exception {}", ex.getMessage());
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String message = fieldErrors.stream()
                .map(item -> messageSource
                        .getMessage("validationExceptionField", null, LocaleContextHolder.getLocale())
                        + item.getField() + messageSource
                        .getMessage("validationExceptionError", null, LocaleContextHolder.getLocale())
                        + item.getDefaultMessage()
                        + messageSource
                        .getMessage("validationExceptionValue", null, LocaleContextHolder.getLocale())
                        + item.getRejectedValue())
                .collect(Collectors.joining("\n "));
        List<String> errors = getStackTrace(ex);
        return new ApiError(errors,
                message,
                messageSource
                        .getMessage(BAD_REQUEST, null, LocaleContextHolder.getLocale()),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler({IllegalArgumentException.class,
            InvalidDataAccessApiUsageException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(Throwable ex) {
        log.info("Validation exception {}", ex.getMessage());
        List<String> errors = getStackTrace(ex);
        return new ApiError(errors,
                ex.getMessage(),
                messageSource
                        .getMessage(BAD_REQUEST, null, LocaleContextHolder.getLocale()),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(DataIntegrityViolationException ex) {
        log.info("Data integrity violation exception {}", ex.getMessage());
        List<String> errors = getStackTrace(ex);
        return new ApiError(errors,
                ex.getMessage(),
                messageSource
                        .getMessage("dataIntegrityExceptionMessage", null, LocaleContextHolder.getLocale()),
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNumberFormatException(MethodArgumentTypeMismatchException ex) {
        log.info("Method argument type mismatch exception {}", ex.getMessage());
        List<String> errors = getStackTrace(ex);
        return new ApiError(errors,
                ex.getMessage(),
                messageSource
                        .getMessage(BAD_REQUEST, null, LocaleContextHolder.getLocale()),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Throwable ex) {
        List<String> errors = getStackTrace(ex);
        log.error(errors.get(0), ex);

        return new ApiError(errors,
                ex.getMessage(),
                messageSource
                        .getMessage("internalServerError", null, LocaleContextHolder.getLocale()),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException ex) {
        log.info("Object not found " + ex.getMessage());
        List<String> errors = getStackTrace(ex);
        return new ApiError(errors,
                ex.getMessage(),
                messageSource.getMessage("requiredObjectWasNotFound", null, LocaleContextHolder.getLocale()),
                HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now());

    }

    private List<String> getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        return Collections.singletonList(stackTrace);
    }
}

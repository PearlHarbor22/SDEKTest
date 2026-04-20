package com.example.sdek_time_tracker.exception;

import com.example.sdek_time_tracker.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений приложения.
 * <p>
 * Перехватывает исключения и преобразует их в единый формат HTTP-ответа (ErrorResponse).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения, когда сущность не найдена.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * Обработка бизнес-ошибок (некорректные данные, логические ошибки).
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * Обработка ошибок валидации @Valid (DTO).
     * Собирает список ошибок по полям.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    /**
     * Обработка ошибок валидации параметров (например, @RequestParam).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    /**
     * Обработка всех прочих непредвиденных ошибок.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.contains(".")
                    ? path.substring(path.lastIndexOf('.') + 1)
                    : path;
            errors.put(field, violation.getMessage());
        });

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    /**
     * Формирует стандартный ответ об ошибке.
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                                                 String message,
                                                                                 Map<String, String> errors) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                errors
        );
        return new ResponseEntity<>(response, status);
    }
}
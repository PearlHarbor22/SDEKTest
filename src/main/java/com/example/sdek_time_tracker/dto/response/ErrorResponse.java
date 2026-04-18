package com.example.sdek_time_tracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Error response")
public record ErrorResponse(

        @Schema(description = "Error timestamp",
                example = "2026-04-18T12:00:00")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code",
                example = "400")
        int status,

        @Schema(description = "HTTP error",
                example = "Bad Request")
        String error,

        @Schema(description = "Error message",
                example = "Validation failed")
        String message,

        @Schema(description = "Validation errors by field")
        Map<String, String> validationErrors
) {
}
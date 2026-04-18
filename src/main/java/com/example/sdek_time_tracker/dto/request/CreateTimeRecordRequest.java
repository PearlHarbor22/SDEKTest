package com.example.sdek_time_tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Request for creating a time record")
public record CreateTimeRecordRequest(

        @Schema(description = "Employee id", example = "1")
        @NotNull(message = "Employee id must not be null")
        @Positive(message = "Employee id must be positive")
        Long employeeId,

        @Schema(description = "Task id", example = "1")
        @NotNull(message = "Task id must not be null")
        @Positive(message = "Task id must be positive")
        Long taskId,

        @Schema(description = "Work start time", example = "2026-04-18T10:00:00")
        @NotNull(message = "Start time must not be null")
        LocalDateTime startTime,

        @Schema(description = "Work end time", example = "2026-04-18T12:00:00")
        @NotNull(message = "End time must not be null")
        LocalDateTime endTime,

        @Schema(description = "Work description", example = "Worked on API implementation")
        @NotBlank(message = "Description must not be blank")
        String description
) {
}

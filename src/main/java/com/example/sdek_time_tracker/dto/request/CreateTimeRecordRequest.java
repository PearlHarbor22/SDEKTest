package com.example.sdek_time_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateTimeRecordRequest(
        @NotNull(message = "Employee id must not be null")
        @Positive(message = "Employee id must be positive")
        Long employeeId,

        @NotNull(message = "Task id must not be null")
        @Positive(message = "Task id must be positive")
        Long taskId,

        @NotNull(message = "Start time must not be null")
        LocalDateTime startTime,

        @NotNull(message = "End time must not be null")
        LocalDateTime endTime,

        @NotBlank(message = "Description must not be blank")
        String description
) {
}

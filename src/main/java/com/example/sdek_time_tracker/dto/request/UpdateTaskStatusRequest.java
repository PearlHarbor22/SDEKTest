package com.example.sdek_time_tracker.dto.request;

import com.example.sdek_time_tracker.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull(message = "Status must not be null")
        TaskStatus status
) {
}
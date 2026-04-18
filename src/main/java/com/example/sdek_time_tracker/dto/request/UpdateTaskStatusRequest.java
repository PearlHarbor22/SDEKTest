package com.example.sdek_time_tracker.dto.request;

import com.example.sdek_time_tracker.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for updating task status")
public record UpdateTaskStatusRequest(

        @Schema(description = "New task status", example = "IN_PROGRESS")
        @NotNull(message = "Status must not be null")
        TaskStatus status
) {
}
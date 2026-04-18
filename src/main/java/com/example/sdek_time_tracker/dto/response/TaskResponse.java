package com.example.sdek_time_tracker.dto.response;

import com.example.sdek_time_tracker.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task response")
public record TaskResponse(
        @Schema(description = "Task id", example = "1")
        Long id,

        @Schema(description = "Task title",
                example = "Implement task creation endpoint")
        String title,

        @Schema(description = "Task description",
                example = "Create REST endpoint for task creation")
        String description,

        @Schema(description = "Task status", example = "NEW")
        TaskStatus status
) {
}

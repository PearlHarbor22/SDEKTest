package com.example.sdek_time_tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for creating task")
public record CreateTaskRequest(

        @Schema(description = "Task title", example = "Implement task creation endpoint")
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @Schema(description = "Task description", example = "Create REST endpoint for task creation")
        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description
) {
}
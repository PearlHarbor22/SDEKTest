package com.example.sdek_time_tracker.dto.response;

import com.example.sdek_time_tracker.entity.TaskStatus;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status
) {
}

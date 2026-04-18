package com.example.sdek_time_tracker.dto.response;

import java.time.LocalDateTime;

public record TimeRecordResponse(
        Long id,
        Long employeeId,
        Long taskId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description
) {
}
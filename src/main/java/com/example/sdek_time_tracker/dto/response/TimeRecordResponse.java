package com.example.sdek_time_tracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Time record response")
public record TimeRecordResponse(

        @Schema(description = "Time record id", example = "1")
        Long id,

        @Schema(description = "Employee id", example = "1")
        Long employeeId,

        @Schema(description = "Task id", example = "1")
        Long taskId,

        @Schema(description = "Work start time",
                example = "2026-04-18T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "Work end time",
                example = "2026-04-18T12:00:00")
        LocalDateTime endTime,

        @Schema(description = "Work description",
                example = "Worked on API implementation")
        String description
) {
}
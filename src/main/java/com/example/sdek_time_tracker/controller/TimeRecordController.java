package com.example.sdek_time_tracker.controller;

import com.example.sdek_time_tracker.dto.request.CreateTimeRecordRequest;
import com.example.sdek_time_tracker.dto.response.TimeRecordResponse;
import com.example.sdek_time_tracker.service.TimeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/time-records")
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    public TimeRecordController(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    @Operation(summary = "Create time record")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeRecordResponse createTimeRecord(@Valid @RequestBody CreateTimeRecordRequest request) {
        return timeRecordService.createTimeRecord(request);
    }

    @Operation(summary = "Get employee time records by period")
    @GetMapping
    public List<TimeRecordResponse> getEmployeeTimeRecords(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return timeRecordService.getEmployeeTimeRecords(employeeId, from, to);
    }
}
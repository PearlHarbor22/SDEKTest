package com.example.sdek_time_tracker.controller;

import com.example.sdek_time_tracker.dto.request.CreateTimeRecordRequest;
import com.example.sdek_time_tracker.dto.response.ErrorResponse;
import com.example.sdek_time_tracker.dto.response.TimeRecordResponse;
import com.example.sdek_time_tracker.service.TimeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/time-records")
@Validated
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    public TimeRecordController(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    @Operation(summary = "Create time record",
                description = "Creates a time record for an employee and a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Time record created",
                            content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = TimeRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid time interval",
                            content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)))
    })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeRecordResponse createTimeRecord(@Valid @RequestBody CreateTimeRecordRequest request) {
        return timeRecordService.createTimeRecord(request);
    }

    @Operation(summary = "Get employee time records by period",
                description = "Returns all time records for the specified employee within the given period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time records retrieved successfully",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(
                                schema = @Schema(implementation = TimeRecordResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid period",
                            content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping
    public List<TimeRecordResponse> getEmployeeTimeRecords(
            @RequestParam @Positive(message = "Employee id must be positive") Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return timeRecordService.getEmployeeTimeRecords(employeeId, from, to);
    }
}

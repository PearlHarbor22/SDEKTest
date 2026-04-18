package com.example.sdek_time_tracker.service;

import com.example.sdek_time_tracker.dto.request.CreateTimeRecordRequest;
import com.example.sdek_time_tracker.dto.response.TimeRecordResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeRecordService {
    TimeRecordResponse createTimeRecord(CreateTimeRecordRequest request);
    List<TimeRecordResponse> getEmployeeTimeRecords(Long employeeId, LocalDateTime from, LocalDateTime to);
}
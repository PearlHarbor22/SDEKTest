package com.example.sdek_time_tracker.service;

import com.example.sdek_time_tracker.dto.request.CreateTimeRecordRequest;
import com.example.sdek_time_tracker.dto.response.TimeRecordResponse;
import com.example.sdek_time_tracker.entity.TimeRecord;
import com.example.sdek_time_tracker.exception.BusinessException;
import com.example.sdek_time_tracker.exception.NotFoundException;
import com.example.sdek_time_tracker.mapper.TaskMapper;
import com.example.sdek_time_tracker.mapper.TimeRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeRecordServiceImplTest {

    @Mock
    private TimeRecordMapper timeRecordMapper;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TimeRecordServiceImpl timeRecordService;

    @Test
    void createTimeRecord_shouldCreateTimeRecord() {
        CreateTimeRecordRequest request = new CreateTimeRecordRequest(
                1L,
                10L,
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                "Worked on API implementation"
        );

        when(taskMapper.existsById(10L)).thenReturn(true);

        doAnswer(invocation -> {
            TimeRecord timeRecord = invocation.getArgument(0);
            timeRecord.setId(100L);
            return null;
        }).when(timeRecordMapper).insert(any(TimeRecord.class));

        TimeRecordResponse response = timeRecordService.createTimeRecord(request);

        ArgumentCaptor<TimeRecord> captor = ArgumentCaptor.forClass(TimeRecord.class);
        verify(timeRecordMapper).insert(captor.capture());

        TimeRecord savedRecord = captor.getValue();
        assertEquals(1L, savedRecord.getEmployeeId());
        assertEquals(10L, savedRecord.getTaskId());
        assertEquals(LocalDateTime.of(2026, 4, 18, 10, 0, 0), savedRecord.getStartTime());
        assertEquals(LocalDateTime.of(2026, 4, 18, 12, 0, 0), savedRecord.getEndTime());
        assertEquals("Worked on API implementation", savedRecord.getDescription());

        assertEquals(100L, response.id());
        assertEquals(1L, response.employeeId());
        assertEquals(10L, response.taskId());
        assertEquals(LocalDateTime.of(2026, 4, 18, 10, 0, 0), response.startTime());
        assertEquals(LocalDateTime.of(2026, 4, 18, 12, 0, 0), response.endTime());
        assertEquals("Worked on API implementation", response.description());
    }

    @Test
    void createTimeRecord_shouldThrowNotFoundExceptionWhenTaskDoesNotExist() {
        CreateTimeRecordRequest request = new CreateTimeRecordRequest(
                1L,
                999L,
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                "Should fail"
        );

        when(taskMapper.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> timeRecordService.createTimeRecord(request)
        );

        assertEquals("Task with id=999 not found", exception.getMessage());
        verify(taskMapper).existsById(999L);
        verify(timeRecordMapper, never()).insert(any());
    }

    @Test
    void createTimeRecord_shouldThrowBusinessExceptionWhenStartTimeIsAfterOrEqualToEndTime() {
        CreateTimeRecordRequest request = new CreateTimeRecordRequest(
                1L,
                10L,
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                "Invalid interval"
        );

        when(taskMapper.existsById(10L)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> timeRecordService.createTimeRecord(request)
        );

        assertEquals("Start time must be before end time", exception.getMessage());
        verify(taskMapper).existsById(10L);
        verify(timeRecordMapper, never()).insert(any());
    }

    @Test
    void getEmployeeTimeRecords_shouldReturnRecordsForPeriod() {
        Long employeeId = 1L;
        LocalDateTime from = LocalDateTime.of(2026, 4, 18, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 4, 18, 23, 59, 59);

        TimeRecord record1 = TimeRecord.builder()
                .id(1L)
                .employeeId(employeeId)
                .taskId(10L)
                .startTime(LocalDateTime.of(2026, 4, 18, 10, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 11, 0, 0))
                .description("Worked on task 1")
                .build();

        TimeRecord record2 = TimeRecord.builder()
                .id(2L)
                .employeeId(employeeId)
                .taskId(11L)
                .startTime(LocalDateTime.of(2026, 4, 18, 12, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 13, 30, 0))
                .description("Worked on task 2")
                .build();

        when(timeRecordMapper.findByEmployeeIdAndPeriod(employeeId, from, to))
                .thenReturn(List.of(record1, record2));

        List<TimeRecordResponse> responses = timeRecordService.getEmployeeTimeRecords(employeeId, from, to);

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).id());
        assertEquals(10L, responses.get(0).taskId());
        assertEquals("Worked on task 1", responses.get(0).description());

        assertEquals(2L, responses.get(1).id());
        assertEquals(11L, responses.get(1).taskId());
        assertEquals("Worked on task 2", responses.get(1).description());

        verify(timeRecordMapper).findByEmployeeIdAndPeriod(employeeId, from, to);
    }

    @Test
    void getEmployeeTimeRecords_shouldThrowBusinessExceptionWhenFromIsAfterTo() {
        Long employeeId = 1L;
        LocalDateTime from = LocalDateTime.of(2026, 4, 19, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 4, 18, 23, 59, 59);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> timeRecordService.getEmployeeTimeRecords(employeeId, from, to)
        );

        assertEquals("Period start must be before period end", exception.getMessage());
        verify(timeRecordMapper, never()).findByEmployeeIdAndPeriod(any(), any(), any());
    }
}

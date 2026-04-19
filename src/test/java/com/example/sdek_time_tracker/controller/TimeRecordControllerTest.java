package com.example.sdek_time_tracker.controller;

import com.example.sdek_time_tracker.dto.request.CreateTimeRecordRequest;
import com.example.sdek_time_tracker.dto.response.TimeRecordResponse;
import com.example.sdek_time_tracker.exception.GlobalExceptionHandler;
import com.example.sdek_time_tracker.exception.NotFoundException;
import com.example.sdek_time_tracker.service.JwtService;
import com.example.sdek_time_tracker.service.TimeRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TimeRecordController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TimeRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TimeRecordService timeRecordService;

    @Test
    void createTimeRecord_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateTimeRecordRequest request = new CreateTimeRecordRequest(
                1L,
                1L,
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                "Worked on API implementation"
        );

        TimeRecordResponse response = new TimeRecordResponse(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                "Worked on API implementation"
        );

        when(timeRecordService.createTimeRecord(request)).thenReturn(response);

        mockMvc.perform(post("/api/time-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.taskId").value(1));
    }

    @Test
    void createTimeRecord_shouldReturn400_whenStartTimeIsMissing() throws Exception {
        String requestJson = """
                {
                  "employeeId": 1,
                  "taskId": 1,
                  "endTime": "2026-04-18T12:00:00",
                  "description": "Worked on API implementation"
                }
                """;

        mockMvc.perform(post("/api/time-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void createTimeRecord_shouldReturn404_whenTaskDoesNotExist() throws Exception {
        CreateTimeRecordRequest request = new CreateTimeRecordRequest(
                1L,
                9999L,
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                "Should fail because task does not exist"
        );

        when(timeRecordService.createTimeRecord(request))
                .thenThrow(new NotFoundException("Task with id=9999 not found"));

        mockMvc.perform(post("/api/time-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id=9999 not found"));
    }

    @Test
    void getEmployeeTimeRecords_shouldReturn200_whenRequestIsValid() throws Exception {
        TimeRecordResponse response = new TimeRecordResponse(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 4, 18, 10, 0, 0),
                LocalDateTime.of(2026, 4, 18, 12, 0, 0),
                "Worked on JWT authentication"
        );

        when(timeRecordService.getEmployeeTimeRecords(
                1L,
                LocalDateTime.of(2026, 4, 18, 0, 0, 0),
                LocalDateTime.of(2026, 4, 18, 23, 59, 59)
        )).thenReturn(List.of(response));

        mockMvc.perform(get("/api/time-records")
                        .param("employeeId", "1")
                        .param("from", "2026-04-18T00:00:00")
                        .param("to", "2026-04-18T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].employeeId").value(1))
                .andExpect(jsonPath("$[0].taskId").value(1));
    }

    @Test
    void getEmployeeTimeRecords_shouldReturn400_whenEmployeeIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/time-records")
                        .param("employeeId", "-1")
                        .param("from", "2026-04-18T00:00:00")
                        .param("to", "2026-04-18T23:59:59"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}

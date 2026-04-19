package com.example.sdek_time_tracker.controller;

import com.example.sdek_time_tracker.dto.request.CreateTaskRequest;
import com.example.sdek_time_tracker.dto.request.UpdateTaskStatusRequest;
import com.example.sdek_time_tracker.dto.response.TaskResponse;
import com.example.sdek_time_tracker.entity.TaskStatus;
import com.example.sdek_time_tracker.exception.GlobalExceptionHandler;
import com.example.sdek_time_tracker.exception.NotFoundException;
import com.example.sdek_time_tracker.service.JwtService;
import com.example.sdek_time_tracker.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TaskController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createTask_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Implement task endpoint",
                "Create REST endpoint for task creation"
        );

        TaskResponse response = new TaskResponse(
                1L,
                "Implement task endpoint",
                "Create REST endpoint for task creation",
                TaskStatus.NEW
        );

        when(taskService.createTask(request)).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Implement task endpoint"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void createTask_shouldReturn400_whenTitleIsBlank() throws Exception {
        String requestJson = """
                {
                  "title": "",
                  "description": "Bad request example"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getTaskById_shouldReturn200_whenTaskExists() throws Exception {
        TaskResponse response = new TaskResponse(
                1L,
                "Implement task endpoint",
                "Create REST endpoint for task creation",
                TaskStatus.NEW
        );

        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Implement task endpoint"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void getTaskById_shouldReturn404_whenTaskDoesNotExist() throws Exception {
        when(taskService.getTaskById(9999L))
                .thenThrow(new NotFoundException("Task with id=9999 not found"));

        mockMvc.perform(get("/api/tasks/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id=9999 not found"));
    }

    @Test
    void updateTaskStatus_shouldReturn200_whenRequestIsValid() throws Exception {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.IN_PROGRESS);

        TaskResponse response = new TaskResponse(
                1L,
                "Implement task endpoint",
                "Create REST endpoint for task creation",
                TaskStatus.IN_PROGRESS
        );

        when(taskService.updateTaskStatus(1L, request)).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTaskStatus_shouldReturn400_whenStatusIsNull() throws Exception {
        String requestJson = """
                {
                  "status": null
                }
                """;

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void updateTaskStatus_shouldReturn404_whenTaskDoesNotExist() throws Exception {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        when(taskService.updateTaskStatus(9999L, request))
                .thenThrow(new NotFoundException("Task with id=9999 not found"));

        mockMvc.perform(patch("/api/tasks/9999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id=9999 not found"));
    }
}
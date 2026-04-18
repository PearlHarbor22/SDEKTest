package com.example.sdek_time_tracker.controller;

import com.example.sdek_time_tracker.dto.request.CreateTaskRequest;
import com.example.sdek_time_tracker.dto.request.UpdateTaskStatusRequest;
import com.example.sdek_time_tracker.dto.response.TaskResponse;
import com.example.sdek_time_tracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create task")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @Operation(summary = "Get task by id")
    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Update task status")
    @PatchMapping("/{id}/status")
    public TaskResponse updateTaskStatus(@PathVariable Long id,
                                         @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateTaskStatus(id, request);
    }
}

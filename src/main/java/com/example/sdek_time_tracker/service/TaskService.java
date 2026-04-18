package com.example.sdek_time_tracker.service;

import com.example.sdek_time_tracker.dto.request.CreateTaskRequest;
import com.example.sdek_time_tracker.dto.request.UpdateTaskStatusRequest;
import com.example.sdek_time_tracker.dto.response.TaskResponse;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request);
    TaskResponse getTaskById(Long id);
    TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request);
}
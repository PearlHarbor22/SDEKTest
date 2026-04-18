package com.example.sdek_time_tracker.service;

import com.example.sdek_time_tracker.dto.request.CreateTaskRequest;
import com.example.sdek_time_tracker.dto.request.UpdateTaskStatusRequest;
import com.example.sdek_time_tracker.dto.response.TaskResponse;
import com.example.sdek_time_tracker.entity.Task;
import com.example.sdek_time_tracker.entity.TaskStatus;
import com.example.sdek_time_tracker.exception.NotFoundException;
import com.example.sdek_time_tracker.mapper.TaskMapper;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(TaskStatus.NEW)
                .build();

        taskMapper.insert(task);
        return toResponse(task);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskMapper.findById(id);
        if (task == null) {
            throw new NotFoundException("Task with id=" + id + " not found");
        }
        return toResponse(task);
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request) {
        int updatedRows = taskMapper.updateStatus(id, request.status());
        if (updatedRows == 0) {
            throw new NotFoundException("Task with id=" + id + " not found");
        }
        return getTaskById(id);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }
}

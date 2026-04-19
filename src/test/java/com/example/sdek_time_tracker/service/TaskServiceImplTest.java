package com.example.sdek_time_tracker.service;

import com.example.sdek_time_tracker.dto.request.CreateTaskRequest;
import com.example.sdek_time_tracker.dto.request.UpdateTaskStatusRequest;
import com.example.sdek_time_tracker.dto.response.TaskResponse;
import com.example.sdek_time_tracker.entity.Task;
import com.example.sdek_time_tracker.entity.TaskStatus;
import com.example.sdek_time_tracker.exception.NotFoundException;
import com.example.sdek_time_tracker.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void createTask_shouldCreateTaskWithNewStatus() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Implement task endpoint",
                "Create endpoint for task creation"
        );

        doAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(1L);
            return null;
        }).when(taskMapper).insert(any(Task.class));

        TaskResponse response = taskService.createTask(request);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskMapper).insert(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        assertEquals("Implement task endpoint", savedTask.getTitle());
        assertEquals("Create endpoint for task creation", savedTask.getDescription());
        assertEquals(TaskStatus.NEW, savedTask.getStatus());

        assertEquals(1L, response.id());
        assertEquals("Implement task endpoint", response.title());
        assertEquals("Create endpoint for task creation", response.description());
        assertEquals(TaskStatus.NEW, response.status());
    }

    @Test
    void getTaskById_shouldReturnTaskResponse() {
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .title("Test task")
                .description("Test description")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskMapper.findById(taskId)).thenReturn(task);

        TaskResponse response = taskService.getTaskById(taskId);

        assertNotNull(response);
        assertEquals(taskId, response.id());
        assertEquals("Test task", response.title());
        assertEquals("Test description", response.description());
        assertEquals(TaskStatus.IN_PROGRESS, response.status());

        verify(taskMapper).findById(taskId);
    }

    @Test
    void getTaskById_shouldThrowNotFoundExceptionWhenTaskDoesNotExist() {
        Long taskId = 999L;
        when(taskMapper.findById(taskId)).thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> taskService.getTaskById(taskId)
        );

        assertEquals("Task with id=999 not found", exception.getMessage());
        verify(taskMapper).findById(taskId);
    }

    @Test
    void updateTaskStatus_shouldUpdateStatusAndReturnUpdatedTask() {
        Long taskId = 1L;
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated task")
                .description("Updated description")
                .status(TaskStatus.DONE)
                .build();

        when(taskMapper.updateStatus(taskId, TaskStatus.DONE)).thenReturn(1);
        when(taskMapper.findById(taskId)).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTaskStatus(taskId, request);

        assertNotNull(response);
        assertEquals(taskId, response.id());
        assertEquals("Updated task", response.title());
        assertEquals("Updated description", response.description());
        assertEquals(TaskStatus.DONE, response.status());

        verify(taskMapper).updateStatus(taskId, TaskStatus.DONE);
        verify(taskMapper).findById(taskId);
    }

    @Test
    void updateTaskStatus_shouldThrowNotFoundExceptionWhenTaskDoesNotExist() {
        Long taskId = 999L;
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.DONE);

        when(taskMapper.updateStatus(taskId, TaskStatus.DONE)).thenReturn(0);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> taskService.updateTaskStatus(taskId, request)
        );

        assertEquals("Task with id=999 not found", exception.getMessage());
        verify(taskMapper).updateStatus(taskId, TaskStatus.DONE);
        verify(taskMapper, never()).findById(any());
    }
}
package com.example.sdek_time_tracker.mapper;

import com.example.sdek_time_tracker.entity.Task;
import com.example.sdek_time_tracker.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class TaskMapperIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("task_tracker_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM time_records");
        jdbcTemplate.execute("DELETE FROM tasks");
    }

    @Test
    void insert_shouldSaveTaskAndGenerateId() {
        Task task = Task.builder()
                .title("Implement task endpoint")
                .description("Create endpoint for task creation")
                .status(TaskStatus.NEW)
                .build();

        taskMapper.insert(task);

        assertNotNull(task.getId());
        assertTrue(task.getId() > 0);
    }

    @Test
    void findById_shouldReturnInsertedTask() {
        Task task = Task.builder()
                .title("Find me")
                .description("Task for findById test")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        taskMapper.insert(task);

        Task foundTask = taskMapper.findById(task.getId());

        assertNotNull(foundTask);
        assertEquals(task.getId(), foundTask.getId());
        assertEquals("Find me", foundTask.getTitle());
        assertEquals("Task for findById test", foundTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void updateStatus_shouldUpdateTaskStatus() {
        Task task = Task.builder()
                .title("Update status task")
                .description("Task for updateStatus test")
                .status(TaskStatus.NEW)
                .build();

        taskMapper.insert(task);

        int updatedRows = taskMapper.updateStatus(task.getId(), TaskStatus.DONE);

        Task updatedTask = taskMapper.findById(task.getId());

        assertEquals(1, updatedRows);
        assertNotNull(updatedTask);
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    void existsById_shouldReturnTrueForExistingTask() {
        Task task = Task.builder()
                .title("Existing task")
                .description("Task for existsById true test")
                .status(TaskStatus.NEW)
                .build();

        taskMapper.insert(task);

        boolean exists = taskMapper.existsById(task.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalseForNonExistingTask() {
        boolean exists = taskMapper.existsById(999999L);

        assertFalse(exists);
    }
}
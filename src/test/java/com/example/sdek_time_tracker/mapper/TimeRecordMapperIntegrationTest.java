package com.example.sdek_time_tracker.mapper;

import com.example.sdek_time_tracker.entity.Task;
import com.example.sdek_time_tracker.entity.TaskStatus;
import com.example.sdek_time_tracker.entity.TimeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class TimeRecordMapperIntegrationTest {

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
    private TimeRecordMapper timeRecordMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM time_records");
        jdbcTemplate.execute("DELETE FROM tasks");
    }

    @Test
    void insert_shouldSaveTimeRecordAndGenerateId() {
        Task task = createTask("Task for time record insert");

        TimeRecord timeRecord = TimeRecord.builder()
                .employeeId(1L)
                .taskId(task.getId())
                .startTime(LocalDateTime.of(2026, 4, 18, 10, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 12, 0, 0))
                .description("Worked on API implementation")
                .build();

        timeRecordMapper.insert(timeRecord);

        assertNotNull(timeRecord.getId());
        assertTrue(timeRecord.getId() > 0);
    }

    @Test
    void findByEmployeeIdAndPeriod_shouldReturnRecordsForEmployeeWithinPeriod() {
        Task task1 = createTask("Task 1");
        Task task2 = createTask("Task 2");

        TimeRecord record1 = TimeRecord.builder()
                .employeeId(1L)
                .taskId(task1.getId())
                .startTime(LocalDateTime.of(2026, 4, 18, 10, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 11, 0, 0))
                .description("Worked on task 1")
                .build();

        TimeRecord record2 = TimeRecord.builder()
                .employeeId(1L)
                .taskId(task2.getId())
                .startTime(LocalDateTime.of(2026, 4, 18, 12, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 13, 0, 0))
                .description("Worked on task 2")
                .build();

        timeRecordMapper.insert(record1);
        timeRecordMapper.insert(record2);

        List<TimeRecord> records = timeRecordMapper.findByEmployeeIdAndPeriod(
                1L,
                LocalDateTime.of(2026, 4, 18, 0, 0, 0),
                LocalDateTime.of(2026, 4, 18, 23, 59, 59)
        );

        assertEquals(2, records.size());

        assertEquals(record1.getId(), records.get(0).getId());
        assertEquals(record2.getId(), records.get(1).getId());
        assertEquals("Worked on task 1", records.get(0).getDescription());
        assertEquals("Worked on task 2", records.get(1).getDescription());
    }

    @Test
    void findByEmployeeIdAndPeriod_shouldReturnEmptyListWhenEmployeeDoesNotMatch() {
        Task task = createTask("Task for wrong employee");

        TimeRecord record = TimeRecord.builder()
                .employeeId(1L)
                .taskId(task.getId())
                .startTime(LocalDateTime.of(2026, 4, 18, 10, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 12, 0, 0))
                .description("Worked on task")
                .build();

        timeRecordMapper.insert(record);

        List<TimeRecord> records = timeRecordMapper.findByEmployeeIdAndPeriod(
                2L,
                LocalDateTime.of(2026, 4, 18, 0, 0, 0),
                LocalDateTime.of(2026, 4, 18, 23, 59, 59)
        );

        assertTrue(records.isEmpty());
    }

    @Test
    void findByEmployeeIdAndPeriod_shouldReturnEmptyListWhenRecordIsOutOfRange() {
        Task task = createTask("Task out of range");

        TimeRecord record = TimeRecord.builder()
                .employeeId(1L)
                .taskId(task.getId())
                .startTime(LocalDateTime.of(2026, 4, 18, 10, 0, 0))
                .endTime(LocalDateTime.of(2026, 4, 18, 12, 0, 0))
                .description("Worked on task")
                .build();

        timeRecordMapper.insert(record);

        List<TimeRecord> records = timeRecordMapper.findByEmployeeIdAndPeriod(
                1L,
                LocalDateTime.of(2026, 4, 18, 12, 30, 0),
                LocalDateTime.of(2026, 4, 18, 13, 30, 0)
        );

        assertTrue(records.isEmpty());
    }

    private Task createTask(String title) {
        Task task = Task.builder()
                .title(title)
                .description("Test task description")
                .status(TaskStatus.NEW)
                .build();

        taskMapper.insert(task);
        return task;
    }
}
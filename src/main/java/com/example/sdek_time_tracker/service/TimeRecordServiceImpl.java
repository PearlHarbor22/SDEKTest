package com.example.sdek_time_tracker.service;

import com.example.sdek_time_tracker.dto.request.CreateTimeRecordRequest;
import com.example.sdek_time_tracker.dto.response.TimeRecordResponse;
import com.example.sdek_time_tracker.entity.TimeRecord;
import com.example.sdek_time_tracker.exception.BusinessException;
import com.example.sdek_time_tracker.exception.NotFoundException;
import com.example.sdek_time_tracker.mapper.TaskMapper;
import com.example.sdek_time_tracker.mapper.TimeRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для работы с записями затраченного времени.
 */
@Service
public class TimeRecordServiceImpl implements TimeRecordService {

    private final TimeRecordMapper timeRecordMapper;
    private final TaskMapper taskMapper;

    public TimeRecordServiceImpl(TimeRecordMapper timeRecordMapper, TaskMapper taskMapper) {
        this.timeRecordMapper = timeRecordMapper;
        this.taskMapper = taskMapper;
    }

    /**
     * Создает запись времени по задаче.
     *
     * @param request данные о затраченном времени
     * @return созданная запись времени
     * @throws NotFoundException если задача не существует
     * @throws BusinessException если интервал времени некорректен
     */
    @Override
    public TimeRecordResponse createTimeRecord(CreateTimeRecordRequest request) {
        if (!taskMapper.existsById(request.taskId())) {
            throw new NotFoundException("Task with id=" + request.taskId() + " not found");
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new BusinessException("Start time must be before end time");
        }

        TimeRecord timeRecord = TimeRecord.builder()
                .employeeId(request.employeeId())
                .taskId(request.taskId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .description(request.description())
                .build();

        timeRecordMapper.insert(timeRecord);
        return toResponse(timeRecord);
    }

    /**
     * Возвращает записи сотрудника за указанный период.
     *
     * @param employeeId идентификатор сотрудника
     * @param from начало периода
     * @param to конец периода
     * @return список записей времени
     * @throws BusinessException если период задан некорректно
     */
    @Override
    public List<TimeRecordResponse> getEmployeeTimeRecords(Long employeeId, LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new BusinessException("Period start must be before period end");
        }

        return timeRecordMapper.findByEmployeeIdAndPeriod(employeeId, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TimeRecordResponse toResponse(TimeRecord timeRecord) {
        return new TimeRecordResponse(
                timeRecord.getId(),
                timeRecord.getEmployeeId(),
                timeRecord.getTaskId(),
                timeRecord.getStartTime(),
                timeRecord.getEndTime(),
                timeRecord.getDescription()
        );
    }
}
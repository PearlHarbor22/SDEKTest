package com.example.sdek_time_tracker.mapper;

import com.example.sdek_time_tracker.entity.TimeRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TimeRecordMapper {

    @Insert("""
            INSERT INTO time_records (employee_id, task_id, start_time, end_time, description)
            VALUES (#{employeeId}, #{taskId}, #{startTime}, #{endTime}, #{description})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TimeRecord timeRecord);

    @Select("""
            SELECT id, employee_id, task_id, start_time, end_time, description
            FROM time_records
            WHERE employee_id = #{employeeId}
            AND start_time < #{to}
            AND end_time > #{from}
            ORDER BY start_time
        """)
    List<TimeRecord> findByEmployeeIdAndPeriod(@Param("employeeId") Long employeeId,
                                               @Param("from") LocalDateTime from,
                                               @Param("to") LocalDateTime to);
}
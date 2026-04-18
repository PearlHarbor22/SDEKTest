package com.example.sdek_time_tracker.mapper;

import com.example.sdek_time_tracker.entity.Task;
import com.example.sdek_time_tracker.entity.TaskStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TaskMapper {

    @Insert("""
            INSERT INTO tasks (title, description, status)
            VALUES (#{title}, #{description}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    @Select("""
            SELECT id, title, description, status
            FROM tasks
            WHERE id = #{id}
            """)
    Task findById(Long id);

    @Update("""
            UPDATE tasks
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") TaskStatus status);

    @Select("""
            SELECT EXISTS (
                SELECT 1
                FROM tasks
                WHERE id = #{id}
            )
            """)
    boolean existsById(Long id);
}
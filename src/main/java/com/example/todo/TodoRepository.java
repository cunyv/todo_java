package com.example.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 根据状态查找待办事项
    List<Todo> findByStatus(TaskStatus status);

    // 根据标题模糊查询
    List<Todo> findByTitleContainingIgnoreCase(String title);

    // 按创建时间降序排列
    List<Todo> findAllByOrderByCreatedAtDesc();

    // 按状态和创建时间降序排列
    List<Todo> findByStatusOrderByCreatedAtDesc(TaskStatus status);
}
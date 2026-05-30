package com.example.todo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.todo.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("todos")
public class Todo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Todo(String title, String description, TaskStatus status, Long userId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

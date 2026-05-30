package com.example.todo.facade;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.todo.dto.TodoCreateDTO;
import com.example.todo.dto.TodoUpdateDTO;
import com.example.todo.entity.Todo;
import com.example.todo.enums.TaskStatus;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import com.example.todo.vo.TodoStatsVO;
import com.example.todo.vo.TodoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TodoFacade {

    private final TodoService todoService;
    private final UserService userService;

    /**
     * 获取当前登录用户ID
     */
    public Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 创建待办事项
     */
    public TodoVO createTodo(TodoCreateDTO dto, Long userId) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(TaskStatus.TODO);
        todo.setUserId(userId);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        todoService.save(todo);
        return toTodoVO(todo);
    }

    /**
     * 更新待办事项
     */
    public TodoVO updateTodo(Long id, TodoUpdateDTO dto, Long userId) {
        Todo todo = getTodoEntity(id, userId);
        if (todo == null) {
            throw new RuntimeException("待办事项未找到，ID: " + id);
        }
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(TaskStatus.valueOf(dto.getStatus()));
        todo.setUpdatedAt(LocalDateTime.now());
        todoService.updateById(todo);
        return toTodoVO(todo);
    }

    /**
     * 更新任务状态
     */
    public TodoVO updateTodoStatus(Long id, TaskStatus status, Long userId) {
        Todo todo = getTodoEntity(id, userId);
        if (todo == null) {
            throw new RuntimeException("待办事项未找到，ID: " + id);
        }
        todo.setStatus(status);
        todo.setUpdatedAt(LocalDateTime.now());
        todoService.updateById(todo);
        return toTodoVO(todo);
    }

    /**
     * 删除待办事项
     */
    public void deleteTodo(Long id, Long userId) {
        Todo todo = getTodoEntity(id, userId);
        if (todo == null) {
            throw new RuntimeException("待办事项未找到，ID: " + id);
        }
        todoService.removeById(id);
    }

    /**
     * 根据ID获取待办事项VO
     */
    public TodoVO getTodoVO(Long id, Long userId) {
        Todo todo = getTodoEntity(id, userId);
        return toTodoVO(todo);
    }

    /**
     * 获取用户待开始的任务
     */
    public List<TodoVO> getTodoTasks(Long userId) {
        return toTodoVOList(listByStatus(userId, TaskStatus.TODO));
    }

    /**
     * 获取用户进行中的任务
     */
    public List<TodoVO> getInProgressTasks(Long userId) {
        return toTodoVOList(listByStatus(userId, TaskStatus.IN_PROGRESS));
    }

    /**
     * 获取用户已完成的任务
     */
    public List<TodoVO> getDoneTasks(Long userId) {
        return toTodoVOList(listByStatus(userId, TaskStatus.DONE));
    }

    /**
     * 搜索用户待办事项
     */
    public List<TodoVO> searchTodos(String keyword, Long userId) {
        List<Todo> list = todoService.list(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .like(Todo::getTitle, keyword)
                        .orderByDesc(Todo::getCreatedAt)
        );
        return toTodoVOList(list);
    }

    /**
     * 获取用户所有待办事项
     */
    public List<TodoVO> getAllTodos(Long userId) {
        List<Todo> list = todoService.list(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .orderByDesc(Todo::getCreatedAt)
        );
        return toTodoVOList(list);
    }

    /**
     * 获取统计信息VO
     */
    public TodoStatsVO getStats(Long userId) {
        long todoCount = getTodoTasks(userId).size();
        long inProgressCount = getInProgressTasks(userId).size();
        long doneCount = getDoneTasks(userId).size();
        return new TodoStatsVO(todoCount, inProgressCount, doneCount, todoCount + inProgressCount + doneCount);
    }

    // ========== 内部方法 ==========

    private Todo getTodoEntity(Long id, Long userId) {
        return todoService.getOne(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getId, id)
                        .eq(Todo::getUserId, userId)
        );
    }

    private List<Todo> listByStatus(Long userId, TaskStatus status) {
        return todoService.list(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, status)
                        .orderByDesc(Todo::getCreatedAt)
        );
    }

    /**
     * Entity → VO
     */
    private TodoVO toTodoVO(Todo todo) {
        if (todo == null) {
            return null;
        }
        return new TodoVO(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getStatus(),
                todo.getStatus().getDisplayName(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }

    private List<TodoVO> toTodoVOList(List<Todo> todos) {
        return todos.stream().map(this::toTodoVO).collect(Collectors.toList());
    }
}

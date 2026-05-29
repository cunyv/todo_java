package com.example.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // 获取所有待办事项
    public List<Todo> getAllTodos() {
        return todoRepository.findAllByOrderByCreatedAtDesc();
    }

    // 根据ID获取待办事项
    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    // 创建新的待办事项
    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    // 更新待办事项
    public Todo updateTodo(Long id, Todo todoDetails) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("待办事项未找到，ID: " + id));

        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setStatus(todoDetails.getStatus());

        return todoRepository.save(todo);
    }

    // 更新任务状态
    public Todo updateTodoStatus(Long id, TaskStatus status) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("待办事项未找到，ID: " + id));
        todo.setStatus(status);
        return todoRepository.save(todo);
    }

    // 删除待办事项
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("待办事项未找到，ID: " + id));
        todoRepository.delete(todo);
    }

    // 获取待开始的任务
    public List<Todo> getTodoTasks() {
        return todoRepository.findByStatusOrderByCreatedAtDesc(TaskStatus.TODO);
    }

    // 获取进行中的任务
    public List<Todo> getInProgressTasks() {
        return todoRepository.findByStatusOrderByCreatedAtDesc(TaskStatus.IN_PROGRESS);
    }

    // 获取已完成的任务
    public List<Todo> getDoneTasks() {
        return todoRepository.findByStatusOrderByCreatedAtDesc(TaskStatus.DONE);
    }

    // 搜索待办事项
    public List<Todo> searchTodos(String keyword) {
        return todoRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // 获取统计信息
    public long getCountByStatus(TaskStatus status) {
        return todoRepository.findByStatus(status).size();
    }
}
package com.example.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // 首页 - 看板视图
    @GetMapping
    public String index(Model model) {
        List<Todo> todoTasks = todoService.getTodoTasks();
        List<Todo> inProgressTasks = todoService.getInProgressTasks();
        List<Todo> doneTasks = todoService.getDoneTasks();

        model.addAttribute("todoTasks", todoTasks);
        model.addAttribute("inProgressTasks", inProgressTasks);
        model.addAttribute("doneTasks", doneTasks);
        model.addAttribute("newTodo", new Todo());

        // 统计信息
        model.addAttribute("todoCount", todoTasks.size());
        model.addAttribute("inProgressCount", inProgressTasks.size());
        model.addAttribute("doneCount", doneTasks.size());
        model.addAttribute("totalCount", todoTasks.size() + inProgressTasks.size() + doneTasks.size());

        return "index";
    }

    // 创建新的待办事项
    @PostMapping("/todos")
    public String createTodo(@ModelAttribute Todo todo) {
        todo.setStatus(TaskStatus.TODO);
        todoService.createTodo(todo);
        return "redirect:/";
    }

    // 更新任务状态（用于拖拽）
    @PostMapping("/todos/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        try {
            Todo todo = todoService.updateTodoStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", todo.getId());
            response.put("status", todo.getStatus().name());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 删除待办事项
    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return "redirect:/";
    }

    // 编辑待办事项页面
    @GetMapping("/todos/{id}/edit")
    public String editTodo(@PathVariable Long id, Model model) {
        Todo todo = todoService.getTodoById(id)
                .orElseThrow(() -> new RuntimeException("待办事项未找到，ID: " + id));
        model.addAttribute("todo", todo);
        return "edit";
    }

    // 更新待办事项
    @PostMapping("/todos/{id}")
    public String updateTodo(@PathVariable Long id, @ModelAttribute Todo todoDetails) {
        todoService.updateTodo(id, todoDetails);
        return "redirect:/";
    }

    // 搜索待办事项
    @GetMapping("/search")
    public String searchTodos(@RequestParam String keyword, Model model) {
        List<Todo> todos = todoService.searchTodos(keyword);
        model.addAttribute("searchResults", todos);
        model.addAttribute("keyword", keyword);

        // 仍然需要显示看板
        model.addAttribute("todoTasks", todoService.getTodoTasks());
        model.addAttribute("inProgressTasks", todoService.getInProgressTasks());
        model.addAttribute("doneTasks", todoService.getDoneTasks());
        model.addAttribute("newTodo", new Todo());

        // 统计信息
        model.addAttribute("todoCount", todoService.getTodoTasks().size());
        model.addAttribute("inProgressCount", todoService.getInProgressTasks().size());
        model.addAttribute("doneCount", todoService.getDoneTasks().size());
        model.addAttribute("totalCount", todoService.getAllTodos().size());

        return "index";
    }
}
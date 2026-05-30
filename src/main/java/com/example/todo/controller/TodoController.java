package com.example.todo.controller;

import com.example.todo.dto.TodoCreateDTO;
import com.example.todo.dto.TodoUpdateDTO;
import com.example.todo.enums.TaskStatus;
import com.example.todo.facade.AuthFacade;
import com.example.todo.facade.TodoFacade;
import com.example.todo.vo.TodoStatsVO;
import com.example.todo.vo.TodoVO;
import com.example.todo.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class TodoController {

    private final TodoFacade todoFacade;
    private final AuthFacade authFacade;

    @GetMapping
    public String index(Model model) {
        Long userId = todoFacade.getCurrentUserId();
        UserVO currentUser = authFacade.getCurrentUserVO();
        List<TodoVO> todoTasks = todoFacade.getTodoTasks(userId);
        List<TodoVO> inProgressTasks = todoFacade.getInProgressTasks(userId);
        List<TodoVO> doneTasks = todoFacade.getDoneTasks(userId);
        TodoStatsVO stats = todoFacade.getStats(userId);

        model.addAttribute("todoTasks", todoTasks);
        model.addAttribute("inProgressTasks", inProgressTasks);
        model.addAttribute("doneTasks", doneTasks);
        model.addAttribute("newTodo", new TodoCreateDTO());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("todoCount", stats.getTodoCount());
        model.addAttribute("inProgressCount", stats.getInProgressCount());
        model.addAttribute("doneCount", stats.getDoneCount());
        model.addAttribute("totalCount", stats.getTotalCount());

        return "index";
    }

    @PostMapping("/todos")
    public String createTodo(@ModelAttribute TodoCreateDTO todoCreateDTO) {
        Long userId = todoFacade.getCurrentUserId();
        todoFacade.createTodo(todoCreateDTO, userId);
        return "redirect:/";
    }

    @PostMapping("/todos/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        try {
            Long userId = todoFacade.getCurrentUserId();
            TodoVO todo = todoFacade.updateTodoStatus(id, status, userId);
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

    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(@PathVariable Long id) {
        Long userId = todoFacade.getCurrentUserId();
        todoFacade.deleteTodo(id, userId);
        return "redirect:/";
    }

    @GetMapping("/todos/{id}/edit")
    public String editTodo(@PathVariable Long id, Model model) {
        Long userId = todoFacade.getCurrentUserId();
        UserVO currentUser = authFacade.getCurrentUserVO();
        TodoVO todo = todoFacade.getTodoVO(id, userId);
        if (todo == null) {
            throw new RuntimeException("待办事项未找到，ID: " + id);
        }
        TodoUpdateDTO todoUpdateDTO = new TodoUpdateDTO();
        todoUpdateDTO.setTitle(todo.getTitle());
        todoUpdateDTO.setDescription(todo.getDescription());
        todoUpdateDTO.setStatus(todo.getStatus().name());

        model.addAttribute("todo", todo);
        model.addAttribute("todoUpdateDTO", todoUpdateDTO);
        model.addAttribute("currentUser", currentUser);
        return "edit";
    }

    @PostMapping("/todos/{id}")
    public String updateTodo(@PathVariable Long id, @ModelAttribute TodoUpdateDTO todoUpdateDTO) {
        Long userId = todoFacade.getCurrentUserId();
        todoFacade.updateTodo(id, todoUpdateDTO, userId);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchTodos(@RequestParam String keyword, Model model) {
        Long userId = todoFacade.getCurrentUserId();
        UserVO currentUser = authFacade.getCurrentUserVO();
        List<TodoVO> todos = todoFacade.searchTodos(keyword, userId);
        TodoStatsVO stats = todoFacade.getStats(userId);

        model.addAttribute("searchResults", todos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("todoTasks", todoFacade.getTodoTasks(userId));
        model.addAttribute("inProgressTasks", todoFacade.getInProgressTasks(userId));
        model.addAttribute("doneTasks", todoFacade.getDoneTasks(userId));
        model.addAttribute("newTodo", new TodoCreateDTO());
        model.addAttribute("todoCount", stats.getTodoCount());
        model.addAttribute("inProgressCount", stats.getInProgressCount());
        model.addAttribute("doneCount", stats.getDoneCount());
        model.addAttribute("totalCount", stats.getTotalCount());

        return "index";
    }
}

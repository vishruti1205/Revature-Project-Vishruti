package org.revature.revado.controller;

import org.revature.revado.dto.TodoItemCreateDTO;
import org.revature.revado.dto.TodoItemResponseDTO;
import org.revature.revado.service.TodoItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoItemController {

    private final TodoItemService todoItemService;

    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    // POST /api/todos
    @PostMapping
    public TodoItemResponseDTO create(@RequestBody TodoItemCreateDTO dto) {
        return todoItemService.createTodo(dto);
    }

    // GET /api/todos
    @GetMapping
    public List<TodoItemResponseDTO> getMyTodos() {
        return todoItemService.getMyTodos();
    }

    // PUT /api/todos/{id}/completed
    @PutMapping("/{id}/completed")
    public TodoItemResponseDTO updateCompleted(@PathVariable String id,
                                               @RequestParam boolean value) {
        return todoItemService.updateCompleted(id, value);
    }

    // PUT /api/todos/{id}
    @PutMapping("/{id}")
    public TodoItemResponseDTO updateTodo(@PathVariable String id,
                                          @RequestBody TodoItemCreateDTO dto) {
        return todoItemService.updateTodo(id, dto);
    }

    // DELETE /api/todos/{id}
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        todoItemService.deleteTodo(id);
        return "Todo deleted";
    }
}

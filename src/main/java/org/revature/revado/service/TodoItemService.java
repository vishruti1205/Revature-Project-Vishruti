package org.revature.revado.service;

import org.revature.revado.dto.TodoItemCreateDTO;
import org.revature.revado.dto.TodoItemResponseDTO;
import org.revature.revado.entity.TodoItem;
import org.revature.revado.entity.User;
import org.revature.revado.repository.TodoItemRepo;
import org.revature.revado.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoItemService {

    private final TodoItemRepo todoItemRepo;
    private final UserRepository userRepository;

    // Inject repositories so service can access database
    public TodoItemService(TodoItemRepo todoItemRepo, UserRepository userRepository) {
        this.todoItemRepo = todoItemRepo;
        this.userRepository = userRepository;
    }

    // CREATE TODO- Create a new todo linked to logged-in user.
    public TodoItemResponseDTO createTodo(TodoItemCreateDTO dto) {

        // Username comes from JWT via SecurityContext
        User user = getLoggedInUser();

        TodoItem todo = new TodoItem();
        todo.setId(java.util.UUID.randomUUID().toString());// Generate unique String ID manually because Hibernate will not auto-generate // IDs when @GeneratedValue is removed and ID type is String

        todo.setTitle(dto.getTitle());

        // Default new todo as incomplete
        todo.setCompleted(false);

        // Attach todo to user
        todo.setUser(user);

        // Save to DB
        TodoItem saved = todoItemRepo.save(todo);

        return toResponseDTO(saved);
    }

    // GET TODOS- Returns only todos belonging to logged-in user.
    public List<TodoItemResponseDTO> getMyTodos() {

        User user = getLoggedInUser();

        return todoItemRepo.findByUser(user)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE COMPLETED- Mark todo complete/incomplete (only owner allowed)
    public TodoItemResponseDTO updateCompleted(String todoId, boolean completed) {

        User user = getLoggedInUser();

        TodoItem todo = todoItemRepo.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // Security check: ensure todo belongs to logged-in user
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to update this todo");
        }

        todo.setCompleted(completed);

        TodoItem saved = todoItemRepo.save(todo);

        return toResponseDTO(saved);
    }

    // DELETE TODO
    public void deleteTodo(String todoId) {

        User user = getLoggedInUser();

        TodoItem todo = todoItemRepo.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to delete this todo");
        }

        todoItemRepo.delete(todo);
    }

    // GET USER FROM JWT- JwtAuthFilter already validated token and stored username in SecurityContext.
    private User getLoggedInUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //  this method convert entity to DTO
    private TodoItemResponseDTO toResponseDTO(TodoItem todo) {

        TodoItemResponseDTO dto = new TodoItemResponseDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setCompleted(todo.isCompleted());

        return dto;
    }
}
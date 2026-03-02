package org.revature.revado.service;

import org.revature.revado.dto.SubtaskCreateDTO;
import org.revature.revado.dto.SubtaskResponseDTO;
import org.revature.revado.entity.SubtaskItem;
import org.revature.revado.entity.TodoItem;
import org.revature.revado.entity.User;
import org.revature.revado.repository.SubtaskItemRepo;
import org.revature.revado.repository.TodoItemRepo;
import org.revature.revado.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubtaskItemService {

    // Repository to access subtask table
    private final SubtaskItemRepo subtaskRepo;

    // Repository to access todo table
    private final TodoItemRepo todoRepo;

    // Repository to access user table
    private final UserRepository userRepository;

    // Constructor injection
    public SubtaskItemService(SubtaskItemRepo subtaskRepo,
                              TodoItemRepo todoRepo,
                              UserRepository userRepository) {
        this.subtaskRepo = subtaskRepo;
        this.todoRepo = todoRepo;
        this.userRepository = userRepository;
    }

    // CREATE SUBTASK
    public SubtaskResponseDTO createSubtask(SubtaskCreateDTO dto) {

        // Get currently logged-in user from JWT
        User user = getLoggedInUser();

        // Find the parent Todo
        TodoItem todo = todoRepo.findById(dto.getTodoId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // SECURITY CHECK:Only allow if logged-in user owns this Todo
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to add subtask to this todo");
        }

        // Create new subtask
        SubtaskItem subtask = new SubtaskItem();
        subtask.setId(UUID.randomUUID().toString()); // Generate UUID
        subtask.setTitle(dto.getTitle());
        subtask.setCompleted(false);
        subtask.setTodoItem(todo); // Link subtask to parent todo

        // Save to database
        SubtaskItem saved = subtaskRepo.save(subtask);

        // Convert entity → DTO and return
        return toResponseDTO(saved);
    }

    // GET ALL SUBTASKS FOR A TODO
    public List<SubtaskResponseDTO> getSubtasks(String todoId) {

        // Get logged-in user
        User user = getLoggedInUser();

        // Find Todo
        TodoItem todo = todoRepo.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // SECURITY CHECK:Only owner can view subtasks
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to view these subtasks");
        }

        // Fetch subtasks and convert to DTO list
        return subtaskRepo.findByTodoItemId(todoId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE COMPLETED STATUS
    public SubtaskResponseDTO updateCompleted(String subtaskId, boolean completed) {

        // Get logged-in user
        User user = getLoggedInUser();

        // Find subtask
        SubtaskItem subtask = subtaskRepo.findById(subtaskId)
                .orElseThrow(() -> new RuntimeException("Subtask not found"));

        // SECURITY CHECK:Only owner of parent Todo can update
        if (!subtask.getTodoItem().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to update this subtask");
        }

        // Update completion status
        subtask.setCompleted(completed);

        // Save updated subtask
        SubtaskItem saved = subtaskRepo.save(subtask);

        return toResponseDTO(saved);
    }

    // DELETE SUBTASK
    public void deleteSubtask(String subtaskId) {

        // Get logged-in user
        User user = getLoggedInUser();

        // Find subtask
        SubtaskItem subtask = subtaskRepo.findById(subtaskId)
                .orElseThrow(() -> new RuntimeException("Subtask not found"));

        // SECURITY CHECK:Only owner can delete
        if (!subtask.getTodoItem().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to delete this subtask");
        }

        // Delete from database
        subtaskRepo.delete(subtask);
    }

    // HELPER: GET LOGGED-IN USER FROM JWT
    private User getLoggedInUser() {

        // Get authentication object from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Extract username from token
        String username = auth.getName();

        // Find user in database
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // CONVERT ENTITY → DTO
    private SubtaskResponseDTO toResponseDTO(SubtaskItem subtask) {

        SubtaskResponseDTO dto = new SubtaskResponseDTO();
        dto.setId(subtask.getId());
        dto.setTitle(subtask.getTitle());
        dto.setCompleted(subtask.isCompleted());

        return dto;
    }
}
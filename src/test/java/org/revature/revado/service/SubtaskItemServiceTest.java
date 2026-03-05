package org.revature.revado.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.revature.revado.dto.SubtaskCreateDTO;
import org.revature.revado.dto.SubtaskResponseDTO;
import org.revature.revado.dto.SubtaskUpdateDTO;
import org.revature.revado.entity.SubtaskItem;
import org.revature.revado.entity.TodoItem;
import org.revature.revado.entity.User;
import org.revature.revado.repository.SubtaskItemRepo;
import org.revature.revado.repository.TodoItemRepo;
import org.revature.revado.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubtaskItemServiceTest {

    // Fake subtask repository for isolated unit testing.
    @Mock
    private SubtaskItemRepo subtaskRepo;

    // Fake todo repository so parent todo lookups are controlled by the test.
    @Mock
    private TodoItemRepo todoRepo;

    // Fake user repository so no real database is used.
    @Mock
    private UserRepository userRepository;

    // Real service object that receives the fake dependencies above.
    @InjectMocks
    private SubtaskItemService subtaskItemService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // Tests creating a subtask for a todo owned by the logged-in user.
    @Test
    void createSubtask_shouldSaveSubtaskForTodoOwner() {
        // Arrange: fake logged-in user.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setTitle("Backend tasks");
        todo.setUser(user);

        SubtaskCreateDTO dto = new SubtaskCreateDTO();
        dto.setTodoId("todo-1");
        dto.setTitle("Write tests");

        SubtaskItem savedSubtask = new SubtaskItem();
        savedSubtask.setId("subtask-1");
        savedSubtask.setTitle("Write tests");
        savedSubtask.setCompleted(false);
        savedSubtask.setTodoItem(todo);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(todoRepo.findById("todo-1")).thenReturn(Optional.of(todo));
        when(subtaskRepo.save(any(SubtaskItem.class))).thenReturn(savedSubtask);

        // Act: create the subtask.
        SubtaskResponseDTO result = subtaskItemService.createSubtask(dto);

        // Assert: saved subtask data should be returned.
        assertEquals("subtask-1", result.getId());
        assertEquals("Write tests", result.getTitle());
        assertFalse(result.isCompleted());

        verify(subtaskRepo).save(any(SubtaskItem.class));
    }

    // Tests fetching subtasks for a todo that belongs to the logged-in user.
    @Test
    void getSubtasks_shouldReturnSubtasksForTodoOwner() {
        // Arrange: fake logged-in user.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setTitle("Backend tasks");
        todo.setUser(user);

        SubtaskItem subtask = new SubtaskItem();
        subtask.setId("subtask-1");
        subtask.setTitle("Learn Mockito");
        subtask.setCompleted(true);
        subtask.setTodoItem(todo);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(todoRepo.findById("todo-1")).thenReturn(Optional.of(todo));
        when(subtaskRepo.findByTodoItemId("todo-1")).thenReturn(List.of(subtask));

        // Act: fetch subtasks for the todo.
        List<SubtaskResponseDTO> result = subtaskItemService.getSubtasks("todo-1");

        // Assert: list should contain the expected subtask.
        assertEquals(1, result.size());
        assertEquals("subtask-1", result.get(0).getId());
        assertEquals("Learn Mockito", result.get(0).getTitle());
        assertTrue(result.get(0).isCompleted());
    }

    // Tests updating the completed status of a subtask owned by the logged-in user.
    @Test
    void updateCompleted_shouldUpdateSubtaskWhenOwnerMatches() {
        // Arrange: fake logged-in user.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setTitle("Backend tasks");
        todo.setUser(user);

        SubtaskItem subtask = new SubtaskItem();
        subtask.setId("subtask-1");
        subtask.setTitle("Write assertions");
        subtask.setCompleted(false);
        subtask.setTodoItem(todo);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(subtaskRepo.findById("subtask-1")).thenReturn(Optional.of(subtask));
        when(subtaskRepo.save(any(SubtaskItem.class))).thenReturn(subtask);

        // Act: mark subtask complete.
        SubtaskResponseDTO result = subtaskItemService.updateCompleted("subtask-1", true);

        // Assert: updated subtask should be complete.
        assertTrue(result.isCompleted());
        verify(subtaskRepo).save(subtask);
    }

    // Tests updating the title of a subtask owned by the logged-in user.
    @Test
    void updateSubtask_shouldUpdateTitleWhenOwnerMatches() {
        // Arrange: fake logged-in user.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setUser(user);

        SubtaskItem subtask = new SubtaskItem();
        subtask.setId("subtask-1");
        subtask.setTitle("Old title");
        subtask.setTodoItem(todo);

        SubtaskUpdateDTO dto = new SubtaskUpdateDTO();
        dto.setTitle("New title");

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(subtaskRepo.findById("subtask-1")).thenReturn(Optional.of(subtask));
        when(subtaskRepo.save(any(SubtaskItem.class))).thenReturn(subtask);

        // Act: update subtask title.
        SubtaskResponseDTO result = subtaskItemService.updateSubtask("subtask-1", dto);

        // Assert: title should be updated.
        assertEquals("New title", result.getTitle());
        verify(subtaskRepo).save(subtask);
    }

    // Tests that deleting a subtask is blocked when the logged-in user is not the owner.
    @Test
    void deleteSubtask_shouldThrowExceptionWhenUserDoesNotOwnSubtask() {
        // Arrange: fake logged-in user.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User loggedInUser = new User();
        loggedInUser.setId("user-1");
        loggedInUser.setUsername("vishruti");

        User differentOwner = new User();
        differentOwner.setId("user-2");
        differentOwner.setUsername("someoneElse");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setTitle("Protected todo");
        todo.setUser(differentOwner);

        SubtaskItem subtask = new SubtaskItem();
        subtask.setId("subtask-1");
        subtask.setTitle("Protected subtask");
        subtask.setTodoItem(todo);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(loggedInUser));
        when(subtaskRepo.findById("subtask-1")).thenReturn(Optional.of(subtask));

        // Act + Assert: non-owner should not be able to delete.
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subtaskItemService.deleteSubtask("subtask-1")
        );

        assertEquals("Not allowed to delete this subtask", exception.getMessage());
        verify(subtaskRepo, never()).delete(any(SubtaskItem.class));
    }
}

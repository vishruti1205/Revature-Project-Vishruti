package org.revature.revado.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.revature.revado.dto.TodoItemCreateDTO;
import org.revature.revado.dto.TodoItemResponseDTO;
import org.revature.revado.entity.TodoItem;
import org.revature.revado.entity.User;
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
class TodoItemServiceTest {

    // Fake todo repository used only in tests.
    @Mock
    private TodoItemRepo todoItemRepo;

    // Fake user repository so no real database is used.
    @Mock
    private UserRepository userRepository;

    // Real service under test with mocked dependencies injected.
    @InjectMocks
    private TodoItemService todoItemService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // Tests creating a todo item for the currently authenticated user.
    @Test
    void createTodo_shouldSaveTodoForLoggedInUser() {
        // Arrange: put a fake logged-in user into Spring Security.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItemCreateDTO dto = new TodoItemCreateDTO();
        dto.setTitle("Finish unit testing");

        TodoItem savedTodo = new TodoItem();
        savedTodo.setId("todo-1");
        savedTodo.setTitle("Finish unit testing");
        savedTodo.setCompleted(false);
        savedTodo.setUser(user);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(todoItemRepo.save(any(TodoItem.class))).thenReturn(savedTodo);

        // Act: call the real service method.
        TodoItemResponseDTO result = todoItemService.createTodo(dto);

        // Assert: check the returned todo data.
        assertEquals("todo-1", result.getId());
        assertEquals("Finish unit testing", result.getTitle());
        assertFalse(result.isCompleted());

        verify(userRepository).findByUsername("vishruti");
        verify(todoItemRepo).save(any(TodoItem.class));
    }

    // Tests retrieving only the todo items owned by the logged-in user.
    @Test
    void getMyTodos_shouldReturnOnlyLoggedInUsersTodos() {
        // Arrange: fake authenticated user.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setTitle("Study Mockito");
        todo.setCompleted(true);
        todo.setUser(user);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(todoItemRepo.findByUser(user)).thenReturn(List.of(todo));

        // Act: load todos for the logged-in user.
        List<TodoItemResponseDTO> result = todoItemService.getMyTodos();

        // Assert: returned list should match mocked data.
        assertEquals(1, result.size());
        assertEquals("todo-1", result.get(0).getId());
        assertEquals("Study Mockito", result.get(0).getTitle());
        assertTrue(result.get(0).isCompleted());
    }

    // Tests marking a todo as completed when the logged-in user owns it.
    @Test
    void updateCompleted_shouldUpdateTodoWhenOwnerMatches() {
        // Arrange: fake authenticated owner.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("vishruti", null)
        );

        User user = new User();
        user.setId("user-1");
        user.setUsername("vishruti");

        TodoItem todo = new TodoItem();
        todo.setId("todo-1");
        todo.setTitle("Complete backend work");
        todo.setCompleted(false);
        todo.setUser(user);

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(todoItemRepo.findById("todo-1")).thenReturn(Optional.of(todo));
        when(todoItemRepo.save(any(TodoItem.class))).thenReturn(todo);

        // Act: mark the todo as completed.
        TodoItemResponseDTO result = todoItemService.updateCompleted("todo-1", true);

        // Assert: the todo should now be complete.
        assertTrue(result.isCompleted());
        verify(todoItemRepo).save(todo);
    }

    // Tests that deleting a todo is rejected when the logged-in user is not the owner.
    @Test
    void deleteTodo_shouldThrowExceptionWhenUserDoesNotOwnTodo() {
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

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(loggedInUser));
        when(todoItemRepo.findById("todo-1")).thenReturn(Optional.of(todo));

        // Act + Assert: deletion should fail for non-owner.
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> todoItemService.deleteTodo("todo-1")
        );

        assertEquals("Not allowed to delete this todo", exception.getMessage());
        verify(todoItemRepo, never()).delete(any(TodoItem.class));
    }
}

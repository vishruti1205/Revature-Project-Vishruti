package org.revature.revado.repository;

import org.revature.revado.entity.TodoItem;
import org.revature.revado.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoItemRepo extends JpaRepository<TodoItem, String> {

    // Get all todos for a specific user
    List<TodoItem> findByUser(User user);
}
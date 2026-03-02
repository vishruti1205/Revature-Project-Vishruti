package org.revature.revado.repository;

import org.revature.revado.entity.SubtaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtaskItemRepo extends JpaRepository<SubtaskItem, String> {

    // Get all subtasks of a specific Todo
    List<SubtaskItem> findByTodoItemId(String todoId);
}
package org.revature.revado.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subtask_items")
@Getter
@Setter
@NoArgsConstructor
public class SubtaskItem {

    // Primary key stored as TEXT (UUID string)
    @Id
    @Column(columnDefinition = "TEXT")
    private String id;

    // Title of the subtask
    @Column(nullable = false)
    private String title;

    // Track completion status
    @Column(nullable = false)
    private boolean completed = false;

    // Many subtasks belong to ONE TodoItem
    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private TodoItem todoItem;
}
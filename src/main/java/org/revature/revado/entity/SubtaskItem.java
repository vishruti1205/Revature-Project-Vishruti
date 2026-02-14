package org.revature.revado.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor // <--- REQUIRED by JPA
@Table(name = "subtask_items")
public class SubtaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String description;

    private boolean isCompleted;

    // Many Subtasks belong to One TodoItem.
    // This creates a "todo_item_id" column in the database to link them.
    @ManyToOne
    @JoinColumn(name = "todo_item_id")
    private TodoItem todoItem;

}

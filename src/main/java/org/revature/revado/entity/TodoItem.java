package org.revature.revado.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "todo_items")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private boolean isCompleted;

    // Many TodoItems belong to One User.
    // @JoinColumn creates a column in this table called "user_id" that links back to the User.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // One TodoItem can have a list of many Subtasks (e.g., "Buy Milk", "Buy Eggs")
    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<SubtaskItem> subtasks;


}

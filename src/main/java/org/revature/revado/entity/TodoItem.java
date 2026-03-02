package org.revature.revado.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "todo_items")
public class TodoItem {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID id;

    @Id
    @Column(columnDefinition = "TEXT")
    private String id;

    @Column(nullable = false)
    private String title;

    private boolean Completed;

    // Many TodoItems belong to One User.
    // @JoinColumn creates a column in this table called "user_id" that links back to the User.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // One TodoItem can have a list of many Subtasks
    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<SubtaskItem> subtasks;


}

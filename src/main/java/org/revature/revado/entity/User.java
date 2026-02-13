package org.revature.revado.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    // One User has a list of many TodoItems.
    // "mappedBy" tells the DB: "Go look at the 'user' field in TodoItem to figure out who owns these."
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TodoItem> todoItems;


}
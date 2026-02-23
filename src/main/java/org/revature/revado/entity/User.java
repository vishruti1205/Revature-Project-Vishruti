package org.revature.revado.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor // <--- REQUIRED by JPA
public class User {

    @Id
    @Column(columnDefinition = "TEXT")
    private String id;
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // One User has a list of many TodoItems.
    // "mappedBy" tells the DB: "Go look at the 'user' field in TodoItem to figure out who owns these."
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TodoItem> todoItems;


}
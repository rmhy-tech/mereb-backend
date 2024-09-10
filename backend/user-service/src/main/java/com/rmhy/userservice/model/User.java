package com.rmhy.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, updatable = false)
    private String username;
    @Column(unique = true, updatable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String firstName, String lastName, String username, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

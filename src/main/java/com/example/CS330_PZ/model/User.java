package com.example.CS330_PZ.model;

import com.example.CS330_PZ.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required.")
    //@Email(message = "Email must be in a valid format.")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Username is required.")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters.")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id")
    )
    private Set<Place> favoritePlaces = new HashSet<>();
}

package com.example.CS330_PZ.repository;

import com.example.CS330_PZ.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u from User u WHERE u.role='USER'")
    List<User> getAllUsers();
}

package com.example.CS330_PZ.integration_tests;

import com.example.CS330_PZ.enums.Role;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.service.ValidatorService;
import com.example.CS330_PZ.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class RegisterUserIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ValidatorService emailValidatorService;

    @Test
    void registerUser_successfulRegistration() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setUsername("johndoe");
        user.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(emailValidatorService.isValidEmail("john.doe@example.com")).thenReturn(true);

        userService.registerUser(user);

        User savedUser = userRepository.findByEmail("john.doe@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("johndoe");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void registerUser_duplicateEmail_throwsException() {
        User existing = new User();
        existing.setFirstName("Jane");
        existing.setLastName("Smith");
        existing.setEmail("jane.smith@example.com");
        existing.setUsername("janesmith");
        existing.setPassword("password");
        existing.setRole(Role.USER);
        userRepository.save(existing);

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("jane.smith@example.com");
        user.setUsername("johndoe");
        user.setPassword("password123");

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already in use!");
    }

    @Test
    void registerUser_invalidEmail_throwsException() {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("invalid-email");
        user.setUsername("johndoe");
        user.setPassword("password123");

        when(emailValidatorService.isValidEmail("invalid-email")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email address!");
    }
}

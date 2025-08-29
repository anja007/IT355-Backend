package com.example.CS330_PZ.unit_tests;

import com.example.CS330_PZ.enums.Role;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.service.UserService;
import com.example.CS330_PZ.service.ValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRegisterUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidatorService emailValidatorService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_HappyFlow_Success() {
        User user = new User();
        user.setFirstName("Anja");
        user.setLastName("Popovic");
        user.setEmail("anja@example.com");
        user.setUsername("anja0");
        user.setPassword("password123");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(emailValidatorService.isValidEmail(user.getEmail())).thenReturn(true);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getFirstName().equals("Anja") &&
                        savedUser.getLastName().equals("Popovic") &&
                        savedUser.getEmail().equals("anja@example.com") &&
                        savedUser.getUsername().equals("anja0") &&
                        savedUser.getPassword().equals("encodedPassword") &&
                        savedUser.getRole() == Role.USER
        ));
    }

    @Test
    void registerUser_Fails_WhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("duplicate@example.com");
        user.setUsername("newuser");
        user.setPassword("password");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(user)
        );
        assertEquals("Email is already in use!", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_Fails_WhenUsernameAlreadyExists() {
        User user = new User();
        user.setEmail("unique@example.com");
        user.setUsername("duplicateUser");
        user.setPassword("password");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(user)
        );
        assertEquals("Username is already in use!", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_Fails_WhenInvalidEmail() {
        User user = new User();
        user.setEmail("anja.popovic.example.com");
        user.setUsername("validuser");
        user.setPassword("password");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(emailValidatorService.isValidEmail(user.getEmail())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(user)
        );
        assertEquals("Invalid email address!", ex.getMessage());

        verify(userRepository, never()).save(any());
    }
}

package com.example.CS330_PZ;


import com.example.CS330_PZ.enums.Role;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.security.JwtTokenProvider;
import com.example.CS330_PZ.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceUnitTests {

    @Test
    public void registerUserHappyFlow(){
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        UserService userService = new UserService(passwordEncoder, userRepository, jwtTokenProvider, authenticationManager);

        User user = new User();
        user.setFirstName("Ana");
        user.setLastName("Nikolic");
        user.setEmail("ananikolic@gmail.com");
        user.setUsername("ana123");
        user.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPass123");

        userService.registerUser(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("Ana", saved.getFirstName());
        assertEquals("Nikolic", saved.getLastName());
        assertEquals("ananikolic@gmail.com", saved.getEmail());
        assertEquals("ana123", saved.getUsername());
        assertEquals("encodedPass123", saved.getPassword());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    @DisplayName("Test if email already exists")
    public void registerUserEmailExistsJUnitTest(){
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        UserService userService = new UserService(passwordEncoder, userRepository, jwtTokenProvider, authenticationManager);

        User existingUser = new User();
        existingUser.setEmail("ananikolic1@gmail.com");

        when(userRepository.findByEmail("ananikolic1@gmail.com")).thenReturn(Optional.of(existingUser));

        User user = new User();
        user.setEmail("ananikolic1@gmail.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Email is already in use!", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());

    }

}

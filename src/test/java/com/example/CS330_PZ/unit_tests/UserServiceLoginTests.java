package com.example.CS330_PZ.unit_tests;

import com.example.CS330_PZ.DTO.LoginRequestDTO;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.security.JwtTokenProvider;
import com.example.CS330_PZ.service.UserService;
import com.example.CS330_PZ.service.ValidatorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTests {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private ValidatorService emailValidatorService;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static LoginRequestDTO loginDto(String username, String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }

    @Test
    void login_validCredentials_returnsJwtAndSetsSecurityContext() {
        LoginRequestDTO dto = loginDto("anja", "password");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("jwt-token-123");

        String token = userService.login(dto);

        assertThat(token).isEqualTo("jwt-token-123");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(auth);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(auth);
    }

    @Test
    void login_blankUsername_throwsBadCredentials() {
        LoginRequestDTO dto = loginDto("", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void login_blankPassword_throwsBadCredentials() {
        LoginRequestDTO dto = loginDto("anja", "");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void login_usernameDoesNotExist_throwsBadCredentials() {
        LoginRequestDTO dto = loginDto("ne_postoji", "whatever");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void login_wrongPassword_throwsBadCredentials() {
        LoginRequestDTO dto = loginDto("anja", "pogresno");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void login_invalidFormat_nullFields_throwsBadCredentials() {
        LoginRequestDTO dto = loginDto(null, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> userService.login(dto));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void login_jwtGenerationFails_propagatesException() {
        LoginRequestDTO dto = loginDto("anja", "password");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth))
                .thenThrow(new RuntimeException("JWT generation failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.login(dto));
        assertThat(ex).hasMessageContaining("JWT generation failure");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(auth);
    }
}

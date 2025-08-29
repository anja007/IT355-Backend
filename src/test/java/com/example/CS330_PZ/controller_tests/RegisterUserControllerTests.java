package com.example.CS330_PZ.controller_tests;


import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.security.JwtTokenProvider;
import com.example.CS330_PZ.service.ValidatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegisterUserControllerTests {

    @Autowired MockMvc mvc;

    @MockBean UserRepository userRepository;
    @MockBean PasswordEncoder passwordEncoder;
    @MockBean ValidatorService emailValidatorService;
    @MockBean AuthenticationManager authenticationManager;
    @MockBean JwtTokenProvider jwtTokenProvider;

    @Test
    void register_integration_success_201() throws Exception {
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("ana")).thenReturn(false);

        when(emailValidatorService.isValidEmail("ana@example.com")).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("$enc$");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        String body = """
        {"firstName":"Ana","lastName":"Anić","email":"ana@example.com","username":"ana","password":"Passw0rd!"}
        """;

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void register_integration_emailExists_400() throws Exception {
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

        String body = """
        {"firstName":"Ana","lastName":"Anić","email":"ana@example.com","username":"ana","password":"password"}
        """;

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email is already in use")));
    }

    @Test
    void login_integration_success_200_token() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("JWT_TOKEN");

        String body = """
        {"username":"ana","password":"Passw0rd!"}
        """;

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("JWT_TOKEN"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_integration_badCreds_401() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        String body = """
        {"username":"ana","password":"WRONG"}
        """;

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }


}

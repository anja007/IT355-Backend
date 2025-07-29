package com.example.CS330_PZ.controller;

import com.example.CS330_PZ.DTO.JwtAuthResponseDTO;
import com.example.CS330_PZ.DTO.LoginRequestDTO;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        if(userRepository.existsByUsername(user.getUsername())){
            return ResponseEntity.badRequest().body("Username already taken!");
        }

        userService.registerUser(user);
        return ResponseEntity.ok("User registered!");
    }

    @PostMapping("/login")
public ResponseEntity<JwtAuthResponseDTO> authenticate(@RequestBody LoginRequestDTO
                                                            loginDto) {
    String token = userService.login(loginDto);
    JwtAuthResponseDTO jwtAuthResponse = new JwtAuthResponseDTO();
    jwtAuthResponse.setAccessToken(token);
    return ResponseEntity.ok(jwtAuthResponse);
}

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        String username = authentication.getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }



}
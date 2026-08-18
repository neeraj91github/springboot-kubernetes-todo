package com.neeraj.todo_backend.controller;

import com.neeraj.todo_backend.dto.UserResponse;
import com.neeraj.todo_backend.model.User;
import com.neeraj.todo_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    public AuthController(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // Conflict
                    .body(Map.of("error", "User with this email already exists"));
        }

        try {
            user.setPasswordHash(encoder.encode(user.getPasswordHash()));
            User savedUser = userRepository.save(user);

            UserResponse response = new UserResponse(savedUser.getUserId(), "User created successfully");
            System.out.println(response.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user", "details", e.getMessage()));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Server is working!";
    }
}

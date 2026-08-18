package com.neeraj.todo_backend.controller;

import com.neeraj.todo_backend.dto.LoginRequest;
import com.neeraj.todo_backend.dto.LoginResponse;
import com.neeraj.todo_backend.model.User;
import com.neeraj.todo_backend.repository.UserRepository;
import com.neeraj.todo_backend.security.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
public class LoginController {

    private JWTUtil jwtUtil;
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty() || !BCrypt.checkpw(request.getPassword(), user.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.get().getUserId(), user.get().getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

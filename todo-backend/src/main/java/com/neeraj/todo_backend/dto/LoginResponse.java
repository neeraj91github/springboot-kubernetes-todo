package com.neeraj.todo_backend.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponse {
    private String token;
}

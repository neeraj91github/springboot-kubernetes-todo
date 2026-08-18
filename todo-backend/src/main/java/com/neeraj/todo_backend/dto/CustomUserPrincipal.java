package com.neeraj.todo_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class CustomUserPrincipal {

    private Long userId;
    private String email;
}

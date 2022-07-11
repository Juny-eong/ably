package com.ably.assignment.verification.controller.dto;

import com.ably.assignment.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    private String identifier;

    private String password;

    public User toUser() {
        return User.builder()
                .identifier(identifier)
                .password(password)
                .build();
    }
}

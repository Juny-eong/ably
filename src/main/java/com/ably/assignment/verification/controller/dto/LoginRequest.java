package com.ably.assignment.verification.controller.dto;

import com.ably.assignment.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "identifier(email or phone number) must not be empty")
    private String identifier;

    @NotBlank(message = "password must not be empty")
    private String password;

    public User toUser() {
        return User.builder()
                .identifier(identifier)
                .password(password)
                .build();
    }
}

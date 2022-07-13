package com.ably.assignment.user.controller.dto;

import com.ably.assignment.user.domain.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    private String email;

    private String password;

    private int verificationCode;

    public User toUser() {
        return User.builder()
                .decryptedEmail(email)
                .password(password)
                .verificationCode(verificationCode)
                .build();
    }
}

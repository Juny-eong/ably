package com.ably.assignment.user.controller.dto;

import com.ably.assignment.user.domain.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "email must not be null")
    private String email;

    @NotBlank(message = "password must not be null")
    private String password;

    @NotNull(message = "verification-code must not be null")
    private Integer verificationCode;

    public User toUser() {
        return User.builder()
                .decryptedEmail(email)
                .password(password)
                .verificationCode(verificationCode)
                .build();
    }
}

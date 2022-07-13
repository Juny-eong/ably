package com.ably.assignment.user.controller.dto;

import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.enumerated.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "email must not be empty")
    private String email;

    @NotBlank(message = "password must not be empty")
    private String password;

    @NotBlank(message = "name must not be empty")
    private String name;

    @Nullable
    private String nickname;

    @NotBlank(message = "phone number must not be empty")
    private String phoneNumber;

    @NotNull(message = "verification code must not be empty")
    private Integer verificationCode;

    @NotNull(message = "gender type must not be empty")
    private Gender gender;


    public User toUser() {
        return User.builder()
                .decryptedEmail(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .decryptedPhoneNumber(phoneNumber)
                .verificationCode(verificationCode)
                .gender(gender)
                .build();
    }

    public void setVerificationCode(int code) {
        this.verificationCode = code;
    }
}

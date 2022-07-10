package com.ably.assignment.user.controller.dto;

import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.enumerated.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    private String email;

    private String password;

    private String name;

    private String nickname;

    private Long phoneNumber;

    private int verificationCode;

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
}

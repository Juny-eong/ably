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
public class UserResponse {

    private String email;

    private String name;

    private String nickname;

    private String phoneNumber;

    private Gender gender;


    public static UserResponse of(User user) {
        return UserResponse.builder()
                .email(user.getDecryptedEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getDecryptedPhoneNumber())
                .gender(user.getGender())
                .build();
    }
}

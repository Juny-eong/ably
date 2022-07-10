package com.ably.assignment.user.controller.dto;

import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;


    public static UserResponse of(User user) {
        return UserResponse.builder()
                .email(user.getDecryptedEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getDecryptedPhoneNumber())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }
}

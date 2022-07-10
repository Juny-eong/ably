package com.ably.assignment.user.controller.dto;

import com.ably.assignment.verification.domain.Verification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VerificationCodeResponse {

    private Integer verificationCode;

    public static VerificationCodeResponse of(Verification verification) {
        return VerificationCodeResponse.builder()
                .verificationCode(verification.getCode())
                .build();
    }
}

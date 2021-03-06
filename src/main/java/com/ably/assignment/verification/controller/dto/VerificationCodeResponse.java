package com.ably.assignment.verification.controller.dto;

import com.ably.assignment.verification.domain.Verification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeResponse {

    private String message;

    public static VerificationCodeResponse of(Verification verification) {
        return VerificationCodeResponse.builder()
                .message(
                        String.format("[Web발신] [에이블리] 인증번호[%d]를 입력해 주세요.", verification.getCode())
                )
                .build();
    }
}

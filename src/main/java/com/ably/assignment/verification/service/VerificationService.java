package com.ably.assignment.verification.service;

import com.ably.assignment.global.config.security.jwt.TokenProvider;
import com.ably.assignment.verification.controller.dto.LoginRequest;
import com.ably.assignment.verification.controller.dto.TokenResponse;
import com.ably.assignment.verification.domain.Verification;
import com.ably.assignment.verification.domain.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${verification.expiration-min}")
    private String expiration;


    /**
     * @param phoneNumber 인증코드를 발급할 핸드폰 번호
     * @return 만료되지 않은 인증코드가 있는 경우 해당 코드 리턴, 없거나 만료된 경우 새로 생성한 후 리턴
     */
    @Transactional
    public Verification getOrCreateCode(Long phoneNumber) {
        return verificationRepository.findByPhoneNumberAndCreatedAtGreaterThan(
                phoneNumber, LocalDateTime.now().minusMinutes(Long.parseLong(expiration)))
                .orElseGet(() -> {
                    final int code = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
                    return verificationRepository.save(Verification.builder()
                            .phoneNumber(phoneNumber)
                            .code(code)
                            .build());
                });
    }


    /**
     * @param phoneNumber 본인 인증을 위한 핸드폰 번호
     * @param code 인증코드
     * @return 해당 핸드폰 번호로 발급받은 인증코드와 인풋의 코드가 일치하고 만료되지 않은 경우 True 리턴
     */
    public boolean checkVerificationCode(Long phoneNumber, int code) {
        return verificationRepository.findById(phoneNumber)
                .orElseThrow(RuntimeException::new)
                .isValid(code, Long.parseLong(expiration));
    }


    public TokenResponse login(LoginRequest request) {
        // 1. 입력값으로 만든 임시 Authentication 객체
        Authentication token = tokenProvider.getTemporalToken(request);
        // 2. 임시 객체로 인증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);

        return tokenProvider.createToken(authentication);
    }
}

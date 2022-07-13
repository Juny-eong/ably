package com.ably.assignment.unit.service;

import com.ably.assignment.global.config.security.jwt.TokenProvider;
import com.ably.assignment.verification.domain.Verification;
import com.ably.assignment.verification.domain.VerificationRepository;
import com.ably.assignment.verification.service.VerificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {
    private static final Pattern NUMBER_FORMAT = Pattern.compile("^[0-9]*$");
    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @InjectMocks
    private VerificationService verificationService;


    @DisplayName("해당 전화번호로 발급받은 유효한 코드가 있는 경우 해탕 코드 리턴")
    @Test
    public void getCodeTest() {
        // given
        ReflectionTestUtils.setField(verificationService, "expirationInMinutes", "10");
        final int code = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        final String number = "123456";
        final Verification verification = Verification.builder()
                .code(code)
                .phoneNumber(number)
                .createdAt(LocalDateTime.now())
                .build();
        given(verificationRepository.findByPhoneNumberAndCreatedAtGreaterThan(any(), any()))
                .willReturn(Optional.of(verification));
        // when
        Verification targetVerification = verificationService.getOrCreateCode(number);

        // then
        assertEquals(targetVerification.getCode(), code);
        assertEquals(targetVerification.getPhoneNumber(), number);
    }


    @DisplayName("발급받은 코드가 있으나 만료된 경우 새로 생성")
    @Test
    public void CreateCodeTest() {
        // given
        ReflectionTestUtils.setField(verificationService, "expirationInMinutes", "10"); // expiration = 10;
        final int code = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        final String number = "123456";
        final Verification verification = Verification.builder()
                .code(code)
                .phoneNumber(number)
                .createdAt(LocalDateTime.now())
                .build();
        given(verificationRepository.findByPhoneNumberAndCreatedAtGreaterThan(any(), any()))
                .willReturn(Optional.empty());
        given(verificationRepository.save(any())).willReturn(verification);
        // when
        Verification targetVerification = verificationService.getOrCreateCode(number);

        // then
        assertEquals(targetVerification.getCode(), verification.getCode());
        assertEquals(targetVerification.getPhoneNumber(), number);
    }

}

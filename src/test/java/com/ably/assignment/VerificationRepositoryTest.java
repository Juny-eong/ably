package com.ably.assignment;

import com.ably.assignment.verification.domain.Verification;
import com.ably.assignment.verification.domain.VerificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@DataJpaTest
public class VerificationRepositoryTest {
    @Autowired
    private VerificationRepository verificationRepository;


    @Test
    public void verificationCodeExpirationTest() {
        // given
        LocalDateTime currentTime = LocalDateTime.now();
        final Verification verification = Verification.builder().code(111111).phoneNumber("123456789").build();

        // when

//        findByPhoneNumberAndCreatedAtGreaterThan
    }
}

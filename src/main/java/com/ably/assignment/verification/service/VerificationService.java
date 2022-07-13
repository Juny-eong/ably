package com.ably.assignment.verification.service;

import com.ably.assignment.global.error.ErrorCode;
import com.ably.assignment.global.error.exception.InvalidPhoneNumberException;
import com.ably.assignment.global.error.exception.InvalidVerificationCodeException;
import com.ably.assignment.verification.domain.Verification;
import com.ably.assignment.verification.domain.VerificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class VerificationService {
    private static final Pattern NUMBER_FORMAT = Pattern.compile("^[0-9]*$");
    private final VerificationRepository verificationRepository;

    @Value("${verification.expiration-min}")
    private String expirationInMinutes;


    /**
     * @param phoneNumber 인증코드를 발급할 핸드폰 번호
     * @return 만료되지 않은 인증코드가 있는 경우 해당 코드 리턴, 없거나 만료된 경우 새로 생성한 후 리턴
     */
    @Transactional
    public Verification getOrCreateCode(String phoneNumber) {
        validatePhoneNumber(phoneNumber);

        return verificationRepository.findByPhoneNumberAndCreatedAtGreaterThan(
                phoneNumber, LocalDateTime.now().minusMinutes(Long.parseLong(expirationInMinutes)))
                .orElseGet(() -> {
                    final int code = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
                    return verificationRepository.save(Verification.builder()
                            .phoneNumber(phoneNumber)
                            .code(code)
                            .build());
                });
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!NUMBER_FORMAT.matcher(phoneNumber).matches()) {
            throw new InvalidPhoneNumberException(ErrorCode.INVALID_NUMBER_TYPE);
        }
    }


    /**
     * 해당 핸드폰 번호로 발급받은 인증코드와 인풋의 코드가 일치하지 않거나 만료된 경우 예외 발생
     * @param phoneNumber 암호화되지 않은 핸드폰 번호
     * @param code 인증코드
     * @throws InvalidVerificationCodeException 코드값이 일치하지 않거나 만료된 경우
     */
    @Transactional
    public void checkIsValidOrThrow(String phoneNumber, int code) {
        final Verification verification = verificationRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new InvalidVerificationCodeException(ErrorCode.INVALID_VERIFICATION_CODE));

        if (verification.isValid(code, Long.parseLong(expirationInMinutes))) {
            verificationRepository.delete(verification);
            return;
        }
        throw new InvalidVerificationCodeException(ErrorCode.INVALID_VERIFICATION_CODE);
    }

}

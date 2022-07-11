package com.ably.assignment.global.error.exception;

import com.ably.assignment.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InvalidVerificationCodeException extends RuntimeException {
    private final ErrorCode errorCode;

}

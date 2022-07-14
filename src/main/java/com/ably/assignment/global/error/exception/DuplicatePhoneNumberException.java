package com.ably.assignment.global.error.exception;

import com.ably.assignment.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DuplicatePhoneNumberException extends RuntimeException {
    private final ErrorCode errorCode;
}

package com.ably.assignment.global.error.exception;

import com.ably.assignment.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class ConversionFailedException extends RuntimeException {
    private final ErrorCode errorCode;
}

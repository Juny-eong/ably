package com.ably.assignment.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_NUMBER_TYPE(HttpStatus.BAD_REQUEST, "invalid phone number"),

    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "invalid verification code"),

    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "email already registered"),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "invalid token"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "there is no user matching the email address"), ;


    private final HttpStatus httpStatus;
    private final String messageDetails;

}

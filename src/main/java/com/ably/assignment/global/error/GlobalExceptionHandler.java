package com.ably.assignment.global.error;

import com.ably.assignment.global.error.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DuplicateUserException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException ex) {
        log.error("handleDuplicateUserException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }


    @ExceptionHandler(value = InvalidPhoneNumberException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidPhoneNumberException(InvalidPhoneNumberException ex) {
        log.error("handleInvalidPhoneNumberException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }


    @ExceptionHandler(value = InvalidTokenException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("handleReviewNotFoundException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }


    @ExceptionHandler(value = InvalidVerificationCodeException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidVerificationCodeException(InvalidVerificationCodeException ex) {
        log.error("handleInvalidVerificationCodeException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }


    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("handleUserNotFoundException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }

}

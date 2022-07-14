package com.ably.assignment.global.error;

import com.ably.assignment.global.error.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Controller layer @Valid 검증 실패 처리
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException - ");

        // @Valid 예외처리의 경우 별도 설정된 예외 메시지를 가져와서 넣어준다.
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST.name())
                        .message(message)
                        .build());
    }

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


    @ExceptionHandler(value = DuplicateEmailException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        log.error("handleDuplicateEmailException - ");
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_PASSWORD);
    }


    @ExceptionHandler(value = DuplicatePhoneNumberException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicatePhoneNumberException(DuplicatePhoneNumberException ex) {
        log.error("handleDuplicatePhoneNumberException - ");
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_PASSWORD);
    }


    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("handleUserNotFoundException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }


    @ExceptionHandler(value = ConversionFailedException.class)
    protected ResponseEntity<ErrorResponse> handleConversionFailedException(ConversionFailedException ex) {
        log.error("handleConversionFailedException - ");
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }


    @ExceptionHandler(value = AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException() {
        log.error("handleAuthenticationException - ");
        return ErrorResponse.toResponseEntity(ErrorCode.AUTH_FAILED);
    }


    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException() {
        log.error("handleAccessDeniedException - ");
        return ErrorResponse.toResponseEntity(ErrorCode.ACCESS_DENIED);
    }


    @ExceptionHandler(value = BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("handleBadCredentialsException - ");
        return ErrorResponse.toResponseEntity(ErrorCode.INVALID_PASSWORD);
    }


}

package com.ably.assignment.verification.controller;

import com.ably.assignment.global.response.ResponseWrapper;
import com.ably.assignment.verification.controller.dto.LoginRequest;
import com.ably.assignment.user.controller.dto.VerificationCodeResponse;
import com.ably.assignment.verification.controller.dto.TokenResponse;
import com.ably.assignment.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping(path = "/verification")
@Controller
public class VerificationController {
    private final VerificationService verificationService;

    @GetMapping(path = "/code")
    public ResponseEntity<VerificationCodeResponse> createVerificationCode(@RequestParam("phone-number") Long phoneNumber) {
        final VerificationCodeResponse response =
                VerificationCodeResponse.of(verificationService.getOrCreateCode(phoneNumber));
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/login")
    public ResponseEntity<ResponseWrapper<TokenResponse>> login(LoginRequest request) {
        TokenResponse response = verificationService.login(request);
        return ResponseWrapper.ok("login success.", response);
    }

}

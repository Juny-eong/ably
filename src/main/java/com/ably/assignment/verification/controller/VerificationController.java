package com.ably.assignment.verification.controller;

import com.ably.assignment.user.controller.dto.VerificationCodeResponse;
import com.ably.assignment.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Controller
public class VerificationController {
    private final VerificationService verificationService;

    @GetMapping(path = "/verification-code")
    public ResponseEntity<VerificationCodeResponse> createVerificationCode(@RequestParam("phone") Long phoneNumber) {
        final VerificationCodeResponse response =
                VerificationCodeResponse.of(verificationService.getOrCreateCode(phoneNumber));
        return ResponseEntity.ok(response);
    }

}

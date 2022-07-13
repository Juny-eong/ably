package com.ably.assignment.verification.controller;

import com.ably.assignment.verification.controller.dto.VerificationCodeResponse;
import com.ably.assignment.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping(path = "/verification")
@Controller
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping(path = "/code")
    public ResponseEntity<VerificationCodeResponse> createVerificationCode(@RequestParam("phone-number") String phoneNumber) {
        final VerificationCodeResponse response =
                VerificationCodeResponse.of(verificationService.getOrCreateCode(phoneNumber));
        return ResponseEntity.ok(response);
    }

}

package com.ably.assignment.verification.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String tokenType;

    private String token;
}
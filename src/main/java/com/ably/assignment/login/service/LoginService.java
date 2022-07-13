package com.ably.assignment.login.service;

import com.ably.assignment.global.config.security.jwt.TokenProvider;
import com.ably.assignment.login.controller.dto.TokenResponse;
import com.ably.assignment.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class LoginService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;


    public TokenResponse login(User user) {

        // 1. 입력값으로 만든 임시 Authentication 객체
        Authentication token = tokenProvider.getTemporalToken(user);
        // 2. 임시 객체로 인증
        Authentication authentication = authenticationManager.authenticate(token);

        return tokenProvider.createToken(authentication);
    }
}

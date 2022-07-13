package com.ably.assignment.login.controller;

import com.ably.assignment.global.response.ResponseWrapper;
import com.ably.assignment.login.controller.dto.LoginRequest;
import com.ably.assignment.login.controller.dto.TokenResponse;
import com.ably.assignment.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(path = "/login")
@Controller
public class LoginController {
    private final LoginService loginService;


    @PostMapping
    public ResponseEntity<ResponseWrapper<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = loginService.login(request.toUser());
        return ResponseWrapper.ok("login success", response);
    }

}

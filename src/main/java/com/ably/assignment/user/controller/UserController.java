package com.ably.assignment.user.controller;

import com.ably.assignment.global.response.ResponseWrapper;
import com.ably.assignment.user.controller.dto.*;
import com.ably.assignment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@RestController
public class UserController {
    private final UserService userService;


    @PostMapping(path = "/sign-up")
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(@RequestBody @Valid UserCreateRequest request) {
        final UserResponse response = UserResponse.of(userService.createUser(request.toUser()));
        return ResponseWrapper.ok("create user success", response);
    }


    @GetMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> getUser() {
        final UserResponse response = UserResponse.of(userService.getUserOrThrow());
        return ResponseWrapper.ok("get user success", response);
    }


    @PatchMapping(path = "/password")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.toUser());
        return ResponseWrapper.ok("reset password success", null);
    }

}

package com.ably.assignment.user.controller;

import com.ably.assignment.global.response.ResponseWrapper;
import com.ably.assignment.user.controller.dto.*;
import com.ably.assignment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(UserCreateRequest request) {
        // validation
        // request.validate();
        final UserResponse response = UserResponse.of(userService.createUser(request.toUser()));
        return ResponseWrapper.ok("create user success", response);
    }



    @GetMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> getUser() {
        final UserResponse response = UserResponse.of(userService.getUserOrThrow());
        return ResponseWrapper.ok("get user success", response);
    }


    @PatchMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> resetPassword(UserPatchRequest request) {
        return null;
    }

}

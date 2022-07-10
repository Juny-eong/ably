package com.ably.assignment.user.controller;

import com.ably.assignment.global.response.ResponseWrapper;
import com.ably.assignment.user.controller.dto.UserCreateRequest;
import com.ably.assignment.user.controller.dto.UserResponse;
import com.ably.assignment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/verification-code")
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(UserCreateRequest request) {
        // validation
//        final UserResponse response = UserResponse.of(userService.createUser(request.toUser()));
//        return ResponseWrapper.ok("create user success", response);
        return null;
    }

}

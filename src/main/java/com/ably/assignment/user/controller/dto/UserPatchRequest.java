package com.ably.assignment.user.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPatchRequest {
    private String name;

    private String password;
}

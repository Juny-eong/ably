package com.ably.assignment.user.service;

import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.global.error.ErrorCode;
import com.ably.assignment.global.error.exception.UserNotFoundException;
import com.ably.assignment.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(SEEDEncoder.encrypt(email))
                .map(user -> CustomPrincipal.builder()
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .password(user.getPassword())
                        .build())
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }


    public UserDetails loadUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(SEEDEncoder.encrypt(phoneNumber))
                .map(user -> CustomPrincipal.builder()
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .password(user.getPassword())
                        .build())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
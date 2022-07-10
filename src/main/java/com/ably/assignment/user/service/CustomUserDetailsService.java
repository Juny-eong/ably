package com.ably.assignment.user.service;

import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                        .password(user.getPassword())
                        .build())
                .orElseThrow(RuntimeException::new);
    }
}
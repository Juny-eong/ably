package com.ably.assignment.user.service;

import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(User user) {

        // 1. check exists
        checkUserExists(user.getEmail());

        // 2. encrypt and save
        user.encryptAll();

        // 3. save and return user
        return userRepository.save(user);
    }


    private void checkUserExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException(); // TODO exception
        }
    }

    public User getUserOrThrow() {
        CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal.getEmail() != null) {
            return userRepository.findByEmail(principal.getEmail())
                    .orElseThrow(RuntimeException::new);
        }
        throw new RuntimeException(); // TODO exception
    }
}

package com.ably.assignment.user.service;

import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.UserRepository;
import com.ably.assignment.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class UserService {
    private final VerificationService verificationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        // 1. check verification code
        verificationService.checkIsValidOrThrow(user.getDecryptedPhoneNumber(), user.getVerificationCode());

        // 2. check user exists
        checkUserExists(user.getDecryptedEmail());

        // 3. encrypt and save
        user.encryptAll(passwordEncoder);

        return userRepository.save(user);
    }


    private void checkUserExists(String email) {
        if (userRepository.findByEmail(SEEDEncoder.encrypt(email)).isPresent()) {
            throw new RuntimeException(); // TODO exception
        }
    }


    public User getUserOrThrow() {
        CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal.getEmail() != null) {
            return userRepository.findByEmail(principal.getEmail()) // token 에는 encrypt 된 메일 형태로 존재
                    .orElseThrow(RuntimeException::new);
        }
        throw new RuntimeException(); // TODO exception
    }


    @Transactional
    public void resetPassword(User user) {
        User targetUser = userRepository.findByEmail(SEEDEncoder.encrypt(user.getDecryptedEmail()))
                .orElseThrow();

        verificationService.checkIsValidOrThrow(targetUser.getDecryptedPhoneNumber(), user.getVerificationCode());

        targetUser.resetPassword(passwordEncoder.encode(user.getPassword()));
    }
}

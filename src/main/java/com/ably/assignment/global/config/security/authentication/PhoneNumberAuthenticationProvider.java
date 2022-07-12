package com.ably.assignment.global.config.security.authentication;

import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PhoneNumberAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new InternalAuthenticationServiceException("Authentication is null");
        }
        if (authentication.getCredentials() == null) {
            throw new AuthenticationCredentialsNotFoundException("Credentials is null");
        }

        final String phoneNumber = ((CustomPrincipal) authentication.getPrincipal()).getPhoneNumber();
        final UserDetails userDetails = userDetailsService.loadUserByPhoneNumber(phoneNumber);

        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.warn("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException("Failed to authenticate since password does not match stored value");
        }

        log.info("success to create PhoneNumberPasswordAuthenticationToken");
        return PhoneNumberPasswordAuthenticationToken
                .authenticated(userDetailsService.loadUserByPhoneNumber(phoneNumber));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneNumberPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

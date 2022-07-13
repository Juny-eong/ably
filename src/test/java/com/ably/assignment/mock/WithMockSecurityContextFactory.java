package com.ably.assignment.mock;


import com.ably.assignment.global.config.security.CustomPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;

/**
 * test 시 WithMockCustomPrincipal 어노테이션을 바탕으로 ctx / authentication 객체를 생성해 주입한다.
 */
public class WithMockSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomPrincipal mockPrincipal) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        CustomPrincipal customPrincipal = new CustomPrincipal();
        customPrincipal.setEmail(mockPrincipal.email());
        customPrincipal.setPassword(mockPrincipal.password());
        customPrincipal.setPhoneNumber(mockPrincipal.phoneNumber());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(customPrincipal, customPrincipal.getPassword(), new ArrayList<>());

        securityContext.setAuthentication(authentication);

        return securityContext;
    }

}

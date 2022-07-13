package com.ably.assignment.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockSecurityContextFactory.class)
public @interface WithMockCustomPrincipal {

    String email() default "test@test.com";

    String password() default "password";

    String phoneNumber() default "01012349876";

}

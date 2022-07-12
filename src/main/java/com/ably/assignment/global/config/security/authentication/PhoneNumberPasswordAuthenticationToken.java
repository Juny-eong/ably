package com.ably.assignment.global.config.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 핸드폰 번호를 식별자로 로그인하는 경우 발급받는 토큰 - PhoneNumberPasswordAuthenticationProvider 에서 처리
 */
public class PhoneNumberPasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private Object credentials;

    public PhoneNumberPasswordAuthenticationToken(
            Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    public static PhoneNumberPasswordAuthenticationToken authenticated(UserDetails userDetails) {
        return new PhoneNumberPasswordAuthenticationToken(userDetails, null, new ArrayList<>());
    }


    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }


    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

}

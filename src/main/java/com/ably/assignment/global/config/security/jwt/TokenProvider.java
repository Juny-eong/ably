package com.ably.assignment.global.config.security.jwt;


import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.verification.controller.dto.LoginRequest;
import com.ably.assignment.verification.controller.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    private final Key key;

    @Value("${jwt.token.expiration-ms")
    private String JWT_EXPIRATION_MS;

    @Value("${jwt.token.type}")
    private String tokenType;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes()); // HMAC-SHA256
    }

    public TokenResponse createToken(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        final Date issuedAt = getCurrentTime();

        return TokenResponse.builder()
                .tokenType(tokenType)
                .token(
                        Jwts.builder()
                                .setAudience(String.valueOf(customPrincipal.getId()))
                                .setSubject(customPrincipal.getEmail())
                                .setIssuedAt(issuedAt)
                                .setExpiration(expirationFrom(issuedAt))
                                .signWith(key, SignatureAlgorithm.HS512)
                                .compact()
                )
                .build();
    }

    private Date getCurrentTime() {
        return new Date();
    }

    private Date expirationFrom(Date currentTime) {
        return new Date(currentTime.getTime() + JWT_EXPIRATION_MS);
    }


    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        CustomPrincipal customPrincipal = CustomPrincipal.builder()
                .email(claims.getSubject())
                .id(Long.valueOf(claims.getAudience()))
                .build();

        log.info("[getAuthentication] user email - {}", customPrincipal.getEmail());

        // authorities 파라미터를 받지 않는 생성자는 isAuthenticated = false 로 설정 -> FORBIDDEN
        return new UsernamePasswordAuthenticationToken(customPrincipal, null, null);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.info("[validationToken] - invalid token");
        }
        return false;
    }


    public Authentication getTemporalToken(LoginRequest request) {
        CustomPrincipal customPrincipal = new CustomPrincipal();
        customPrincipal.setEmail(request.getEmail());
        return new UsernamePasswordAuthenticationToken(customPrincipal, request.getPassword(), null);
    }
}

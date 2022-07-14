package com.ably.assignment.global.config.security.jwt;


import com.ably.assignment.global.config.security.CustomPrincipal;
import com.ably.assignment.global.config.security.authentication.PhoneNumberPasswordAuthenticationToken;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.global.error.ErrorCode;
import com.ably.assignment.global.error.exception.InvalidTokenException;
import com.ably.assignment.login.controller.dto.TokenResponse;
import com.ably.assignment.user.domain.User;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TokenProvider {
    private static final Pattern NUMBER_FORMAT = Pattern.compile("^[0-9]*$");
    private final Key key;

    @Value("${jwt.token.expiration-ms}")
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
                                .setAudience(String.valueOf(customPrincipal.getPhoneNumber()))
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
        return new Date(currentTime.getTime() + Long.parseLong(JWT_EXPIRATION_MS));
    }


    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        CustomPrincipal customPrincipal = CustomPrincipal.builder()
                .email(claims.getSubject())
                .build();

        log.info("[getAuthentication] user email - {}", SEEDEncoder.decrypt(customPrincipal.getEmail()));

        // authorities 파라미터를 받지 않는 생성자는 isAuthenticated = false 로 설정 -> FORBIDDEN
        return new UsernamePasswordAuthenticationToken(customPrincipal, null, new ArrayList<>());
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
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
    }


    public Authentication getTemporalToken(User user) {
        return NUMBER_FORMAT.matcher(user.getIdentifier()).matches()
                ? getTokenByPhoneNumber(user)
                : getTokenByEmail(user);
    }


    private Authentication getTokenByPhoneNumber(User user) {
        log.info("phone number token");
        CustomPrincipal customPrincipal = new CustomPrincipal();
        customPrincipal.setPhoneNumber(user.getIdentifier());
        return new PhoneNumberPasswordAuthenticationToken(customPrincipal, user.getPassword(), new ArrayList<>());
    }

    private Authentication getTokenByEmail(User user) {
        log.info("email token");
        CustomPrincipal customPrincipal = new CustomPrincipal();
        customPrincipal.setEmail(user.getIdentifier());
        return new UsernamePasswordAuthenticationToken(customPrincipal, user.getPassword(), new ArrayList<>());
    }

}

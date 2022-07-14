package com.ably.assignment.global.config.security.jwt;

import com.ably.assignment.global.error.ErrorCode;
import com.ably.assignment.global.error.ErrorResponse;
import com.ably.assignment.global.error.exception.InvalidTokenException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);

        log.info("\njwt: {}", jwt);

        try {
            // validation & set context
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                /**
                 * It is important to create a new SecurityContext instance instead of using
                 * `SecurityContextHolder.getContext().setAuthentication(authentication)`
                 * to avoid race conditions across multiple threads.
                 */
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }

            filterChain.doFilter(request, response);

        }
        catch (InvalidTokenException ex) {
            log.error("[token exception] - {}", ex.getErrorCode().getMessageDetails());
            response.setStatus(ex.getErrorCode().getHttpStatus().value());
            response.getWriter().write(writeErrorCodeToString(ex.getErrorCode()));
        }

    }


    @Nullable
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private String writeErrorCodeToString(ErrorCode errorCode) throws JsonProcessingException {
        return objectMapper.writeValueAsString(ErrorResponse.toResponseEntity(errorCode));
    }

}

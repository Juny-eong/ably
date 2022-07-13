package com.ably.assignment.global.config.security;


import com.ably.assignment.global.config.security.authentication.PhoneNumberAuthenticationProvider;
import com.ably.assignment.global.config.security.jwt.JwtFilter;
import com.ably.assignment.global.config.security.jwt.TokenProvider;
import com.ably.assignment.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;

    /**
     * defaultErrorAttributes, HandlerExceptionResolver 두 개의 빈 존재
     */
    @Autowired
    @Qualifier(value = "handlerExceptionResolver")
    private HandlerExceptionResolver resolver;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /**
     * UserNamePasswordAuthenticationToken 검증을 위한 daoAuthenticationProvider 빈 등록
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    /**
     * SecurityFilterChain 이 아닌, 서비스 레이어에서 authenticate 하기 위한 ProviderManager 빈 등록
     * user-defined authenticationProvider list 를 생성자를 통해 주입해야 한다.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                daoAuthenticationProvider(),
                new PhoneNumberAuthenticationProvider(userDetailsService, passwordEncoder())));
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/h2-console/**");
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable() // rest server
                .formLogin().disable() // not form login
                .httpBasic().disable() // authentication - bearer
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no use session

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())

                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**",
                        "/login",
                        "/verification/code",
                        "/users/sign-up",
                        "/users/password").permitAll()
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 유효한 자격 증명 없이 접근하는 경우에 대한 핸들링 -> 401 UNAUTHORIZED
     *
     * security filter chain 에서 발생하는 예외는 dispatcher servlet 에서 @ExceptionHandler 로 감지할 수 없기 때문에
     * 직접 handlerExceptionResolver 에 위임해 globalExceptionHandler 로 처리.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authenticationException) ->
                resolver.resolveException(request, response, null, authenticationException);
    }


    /**
     * 권한이 없는 리소스에 접근하는 경우에 대한 핸들링 -> 403 FORBIDDEN
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                resolver.resolveException(request, response, null, accessDeniedException);
    }

}

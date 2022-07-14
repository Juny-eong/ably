package com.ably.assignment.unit.controller;

import com.ably.assignment.global.config.security.SecurityConfig;
import com.ably.assignment.login.controller.LoginController;
import com.ably.assignment.login.controller.dto.LoginRequest;
import com.ably.assignment.login.controller.dto.TokenResponse;
import com.ably.assignment.login.service.LoginService;
import com.ably.assignment.mock.WithMockCustomPrincipal;
import com.ably.assignment.verification.controller.VerificationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = LoginController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
public class LoginControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoginService loginService;


    @DisplayName("로그인 테스트")
    @WithMockCustomPrincipal
    @Test
    public void loginTest() throws Exception {
        // given
        final TokenResponse response = TokenResponse.builder().tokenType("Bearer").token("jfienv").build();
        given(loginService.login(any())).willReturn(response);
        final LoginRequest loginRequest = LoginRequest.builder()
                .identifier("test@test.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("200"),
                        jsonPath("$.message").value("login success"));
    }

}

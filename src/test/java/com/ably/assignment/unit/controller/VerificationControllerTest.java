package com.ably.assignment.unit.controller;

import com.ably.assignment.global.config.security.SecurityConfig;
import com.ably.assignment.mock.WithMockCustomPrincipal;
import com.ably.assignment.verification.controller.VerificationController;
import com.ably.assignment.verification.controller.dto.LoginRequest;
import com.ably.assignment.verification.domain.Verification;
import com.ably.assignment.verification.service.VerificationService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = VerificationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
public class VerificationControllerTest {
    @MockBean
    private VerificationService verificationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;


    @DisplayName("핸드폰 번호로 본인인증번호 발급")
    @WithMockCustomPrincipal
    @Test
    public void getVerificationNumberTest() throws Exception {
        // given
        final Verification verification = Verification.builder().code(123456).build();
        given(verificationService.getOrCreateCode(any())).willReturn(verification);

        // when & then
        mockMvc.perform(
                get("/verification/code")
                        .param("phone-number", "01012349876")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").exists()
                )
                .andDo(print())
                .andReturn();
    }


    @DisplayName("로그인 테스트")
    @WithMockCustomPrincipal
    @Test
    public void loginTest() throws Exception {
        // given
        final LoginRequest loginRequest = LoginRequest.builder()
                .identifier("test@test.com")
                .password("password")
                .build();

        // when & then
        mockMvc.perform(
                post("/verification/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("200"),
                        jsonPath("$.message").value("login success")
                )
                .andDo(print())
                .andReturn();
    }

}

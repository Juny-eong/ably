package com.ably.assignment.unit.controller;

import com.ably.assignment.global.config.security.SecurityConfig;
import com.ably.assignment.mock.WithMockCustomPrincipal;
import com.ably.assignment.verification.controller.VerificationController;
import com.ably.assignment.verification.domain.Verification;
import com.ably.assignment.verification.service.VerificationService;

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
                post("/verification/code")
                        .param("phone-number", "01012349876")
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").exists());
    }


}

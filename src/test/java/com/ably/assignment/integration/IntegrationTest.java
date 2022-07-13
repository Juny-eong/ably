package com.ably.assignment.integration;

import com.ably.assignment.global.response.ResponseWrapper;
import com.ably.assignment.user.controller.dto.ResetPasswordRequest;
import com.ably.assignment.user.controller.dto.UserCreateRequest;
import com.ably.assignment.user.domain.UserRepository;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.verification.controller.dto.LoginRequest;
import com.ably.assignment.verification.domain.VerificationRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationRepository verificationRepository;

    private static final String phoneNumber = "01012349876";
    private static final String userEmail = "test@test.com";



    @DisplayName("핸드폰 번호로 본인인증코드를 발급받는다.")
    @Test
    public void getVerificationCodeTest() throws Exception {
        // given & when
        final MvcResult result = mockMvc.perform(
                get("/verification/code")
                        .param("phone-number", phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").exists())
                .andReturn();

        // then
        final int verificationCode = verificationRepository.findById(phoneNumber).orElseThrow().getCode();
        assertTrue(
                result.getResponse().getContentAsString().contains(String.valueOf(verificationCode)));
    }


    @DisplayName("발급받은 코드로 회원가입")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    @Nested
    class SignUpTest {

        @DisplayName("1. 인증코드가 유효한 경우 정상적으로 회원가입")
        @Test
        public void signUpWithVerificationCodeTest_success() throws Exception {
            // given
            mockMvc.perform(
                    get("/verification/code")
                            .param("phone-number", phoneNumber)
                            .contentType(MediaType.APPLICATION_JSON));
            final int verificationCode = verificationRepository.findById(phoneNumber).orElseThrow().getCode();

            // when
            mockMvc.perform(
                    post("/users/sign-up")
                            .content(objectMapper.writeValueAsString(getUserCreateRequestWithCode(verificationCode)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.message").value("create user success"));
        }


        @DisplayName("2. 인증코드가 유효하지 않은 경우 예외 발생")
        @Test
        public void signUpWithVerificationCodeTest_fail1() throws Exception {
            // given
            mockMvc.perform(
                    get("/verification/code")
                            .param("phone-number", phoneNumber)
                            .contentType(MediaType.APPLICATION_JSON));
            final int verificationCode = verificationRepository.findById(phoneNumber).orElseThrow().getCode();

            // when & then
            mockMvc.perform(
                    post("/users/sign-up")
                            .content(objectMapper.writeValueAsString(getUserCreateRequestWithCode(verificationCode - 1)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("invalid verification code"));
        }


        @DisplayName("3. 인증코드를 발급받지 않은 채 회원가입 시도할 경우 예외 발생")
        @Test
        public void signUpWithVerificationCodeTest_fail2() throws Exception {
            // given & when
            mockMvc.perform(
                    post("/users/sign-up")
                            .content(objectMapper.writeValueAsString(getUserCreateRequestWithCode(0)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("invalid verification code"));
        }
    }


    @DisplayName("식별자와 비밀번호로 로그인")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class)
    @Nested
    class LoginTest {

        @DisplayName("1. 이메일로 로그인")
        @Test
        public void loginWithEmailTest() throws Exception {
            // given
            SignUpTest signUpTest = new SignUpTest();
            signUpTest.signUpWithVerificationCodeTest_success();
            assertNotNull(userRepository.findByEmail("test@test.com"));

            // when & then
            mockMvc.perform(
                    post("/verification/login")
                            .content(objectMapper.writeValueAsString(getLoginRequest(userEmail)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.message").value("login success"));
        }


        @DisplayName("2. 핸드폰번호로 로그인")
        @Test
        public void loginWithPhoneNumberTest() throws Exception {
            // given
            SignUpTest signUpTest = new SignUpTest();
            signUpTest.signUpWithVerificationCodeTest_success();
            assertNotNull(userRepository.findByEmail(userEmail));

            // when & then
            mockMvc.perform(
                    post("/verification/login")
                            .content(objectMapper.writeValueAsString(getLoginRequest(userEmail)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.message").value("login success"));
        }


        @DisplayName("3. 잘못된 식별자로 로그인 할 경우 예외(401) 발생")
        @Test
        public void loginWithInvalidIdentifierTest() throws Exception {
            // given
            SignUpTest signUpTest = new SignUpTest();
            signUpTest.signUpWithVerificationCodeTest_success();
            assertNotNull(userRepository.findByEmail(userEmail));

            // when & then
            mockMvc.perform(
                    post("/verification/login")
                            .content(objectMapper.writeValueAsString(getLoginRequest("wrongEmail@test.com")))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.message").value("please check identifier / password"));
        }


        @DisplayName("4. 잘못된 비밀번호로 로그인 할 경우 예외(401) 발생")
        @Test
        public void loginWithInvalidPasswordTest() throws Exception {
            // given
            SignUpTest signUpTest = new SignUpTest();
            signUpTest.signUpWithVerificationCodeTest_success();
            assertNotNull(userRepository.findByEmail(userEmail));

            // when & then
            mockMvc.perform(
                    post("/verification/login")
                            .content(objectMapper.writeValueAsString(
                                    getLoginRequestWithInvalidPassword("wrongPassword")))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.message")
                                    .value("Failed to authenticate since password does not match stored value"));
        }


    }


    @DisplayName("jwt로 회원정보 조회")
    @Test
    public void getUserInfoTest() throws Exception {
        // given
        SignUpTest signUpTest = new SignUpTest();
        signUpTest.signUpWithVerificationCodeTest_success();
        assertNotNull(userRepository.findByEmail("test@test.com"));

        MvcResult result = mockMvc.perform(
                post("/verification/login")
                        .content(objectMapper.writeValueAsString(getLoginRequest(userEmail)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("login success")
                )
                .andReturn();

        ResponseWrapper wrapper = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseWrapper.class);
        final String token = (String) ((Map) wrapper.getData()).get("token");
        System.out.println(token);

        // when & then
        mockMvc.perform(
                get("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("get user success"),
                        jsonPath("$.data.email").value(userEmail)
                );
    }


    @DisplayName("본인인증코드로 비밀번호 재설정")
    @Test
    public void resetPasswordWithVerificationCodeTest() throws Exception {
        // given
        SignUpTest signUpTest = new SignUpTest();
        signUpTest.signUpWithVerificationCodeTest_success();
        assertNotNull(userRepository.findByEmail("test@test.com"));

        // 본인인증코드 발급
        mockMvc.perform(
                get("/verification/code")
                        .param("phone-number", phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON));
        final int verificationCode = verificationRepository.findById(phoneNumber).orElseThrow().getCode();
        final ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                .email(userEmail)
                .password("new-password")
                .verificationCode(verificationCode)
                .build();

        // when & then
        mockMvc.perform(
                patch("/users/password")
                        .content(objectMapper.writeValueAsString(resetPasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("reset password success"));
    }



    private UserCreateRequest getUserCreateRequestWithCode(int code) {
        return UserCreateRequest.builder()
                .email(userEmail)
                .password("password")
                .verificationCode(code)
                .phoneNumber("01012349876")
                .name("test")
                .nickname("bob")
                .gender(Gender.MALE)
                .build();
    }

    private LoginRequest getLoginRequest(String identifier) {
        return LoginRequest.builder()
                .identifier(identifier)
                .password("password")
                .build();
    }


    private LoginRequest getLoginRequestWithInvalidPassword(String wrongPassword) {
        return LoginRequest.builder()
                .identifier(userEmail)
                .password(wrongPassword)
                .build();
    }

}

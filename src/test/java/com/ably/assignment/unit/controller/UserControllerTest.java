package com.ably.assignment.unit.controller;

import com.ably.assignment.global.config.security.SecurityConfig;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.mock.WithMockCustomPrincipal;
import com.ably.assignment.user.controller.UserController;
import com.ably.assignment.user.controller.dto.ResetPasswordRequest;
import com.ably.assignment.user.controller.dto.UserCreateRequest;
import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.user.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private User user;

    private MockedStatic<SEEDEncoder> mockedStatic;

    /**
     * static method stubbing
     */
    @BeforeEach
    public void setup() {
        mockedStatic = mockStatic(SEEDEncoder.class);
        given(SEEDEncoder.encrypt(any())).willReturn("encrypted");
        given(SEEDEncoder.decrypt(any())).willReturn("decrypted");
        user = getMockUser();
    }

    /**
     * clear resource
     */
    @AfterEach
    public void clear() {
        mockedStatic.close();
    }


    @DisplayName("유저 정보로 회원 가입 및 유저 생성")
    @WithMockCustomPrincipal
    @Test
    public void signUpTest() throws Exception {
        // given
        given(userService.createUser(any())).willReturn(user);

        // when & then
        mockMvc.perform(
                post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUserCreateRequest())).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("200"),
                        jsonPath("$.message").value("create user success"));
    }


    @DisplayName("유저 정보 조회")
    @WithMockCustomPrincipal
    @Test
    public void getUserTest() throws Exception {
        // given
        given(userService.getUserOrThrow()).willReturn(user);

        // when & then
        mockMvc.perform(
                get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("200"),
                        jsonPath("$.message").value("get user success"));
    }


    @DisplayName("유저의 비밀번호 변경")
    @WithMockCustomPrincipal
    @Test
    public void resetPasswordTest() throws Exception {
        // given
        willDoNothing().given(userService).resetPassword(any());
        final ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("test@test.com")
                .password("password")
                .verificationCode(123456)
                .build();

        // when & then
        mockMvc.perform(
                patch("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("200"),
                        jsonPath("$.message").value("reset password success"));
    }


    private User getMockUser() {
        return User.builder()
                .email("test@test.com")
                .phoneNumber("01012349876")
                .name("test")
                .nickname("bob")
                .gender(Gender.MALE)
                .build();
    }

    private UserCreateRequest getUserCreateRequest() {
        return UserCreateRequest.builder()
                .email("test@test.com")
                .password("password")
                .verificationCode(123456)
                .phoneNumber("01012349876")
                .name("test")
                .nickname("bob")
                .gender(Gender.MALE)
                .build();
    }
}

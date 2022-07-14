package com.ably.assignment.unit.service;

import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.global.error.exception.DuplicateEmailException;
import com.ably.assignment.global.error.exception.DuplicateUserException;
import com.ably.assignment.global.error.exception.UserNotFoundException;
import com.ably.assignment.mock.WithMockCustomPrincipal;
import com.ably.assignment.user.domain.User;
import com.ably.assignment.user.domain.UserRepository;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.user.service.UserService;
import com.ably.assignment.verification.service.VerificationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @Mock
    private VerificationService verificationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private MockedStatic<SEEDEncoder> mockedStatic;
    private User user;


    @BeforeEach
    public void setup() {
        mockedStatic = mockStatic(SEEDEncoder.class);
        given(SEEDEncoder.encrypt(any())).willReturn("encrypted");
        given(SEEDEncoder.decrypt(any())).willReturn("decrypted");
        user = getMockUser();
    }

    @AfterEach
    public void clear() {
        mockedStatic.close();
    }


    @DisplayName("중복된 이메일이 없는 경우 정상 생성")
    @Test
    public void createUserTest() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());
        given(userRepository.save(any())).willReturn(user);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        willDoNothing().given(verificationService).checkIsValidOrThrow(any(), anyInt());

        // when
        final User createdUser = userService.createUser(user);

        // then
        assertEquals("encrypted", createdUser.getEmail());
        assertEquals("encrypted", createdUser.getPhoneNumber());
        assertEquals(user.getName(), createdUser.getName());

    }


    @DisplayName("이미 등록된 이메일인 경우 예외 발생")
    @Test
    public void createUserTestWithAlreadyExists() {
        // given
        willDoNothing().given(verificationService).checkIsValidOrThrow(any(), anyInt());
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user)); // 유저 이미 존재

        // when & then
        assertThatThrownBy(() -> userService.createUser(user)).isInstanceOf(DuplicateEmailException.class);

    }


    @WithMockCustomPrincipal
    @DisplayName("이메일로 유저를 조회한다.")
    @Test
    public void getUserTest() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));

        // when
        final User findUser = userService.getUserOrThrow();

        // then
        assertNotNull(findUser);
        assertEquals(user.getName(), findUser.getName());

    }


    @WithMockCustomPrincipal
    @DisplayName("존재하지 않는 유저 조회 시 예외 발생")
    @Test
    public void getUserWithNoExistsUserInfoTest() {
        // given
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserOrThrow()).isInstanceOf(UserNotFoundException.class);

    }


    @DisplayName("유저의 패스워드를 재설정한다.")
    @Test
    public void resetPasswordTest() {
        // given
        final String passwordBeforeChange = user.getPassword();
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.encode(any())).willReturn("encoded-password");
        willDoNothing().given(verificationService).checkIsValidOrThrow(any(), anyInt());

        // when
        userService.resetPassword(user);

        // then
        assertNotEquals(passwordBeforeChange, user.getPassword());
        assertEquals(user.getPassword(), "encoded-password");

    }


    private User getMockUser() {
        return User.builder()
                .email("test@test.com")
                .password("password")
                .phoneNumber("01012349876")
                .name("test")
                .nickname("bob")
                .gender(Gender.MALE)
                .build();
    }

}

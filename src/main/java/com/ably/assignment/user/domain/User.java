package com.ably.assignment.user.domain;


import com.ably.assignment.global.base.BaseTimeEntity;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.user.domain.enumerated.GenderConverter;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_phone_no", columnList = "phoneNumber")
        }
)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String name;

    private String nickname;

    @Column(unique = true)
    private String phoneNumber; // encrypt 형태 저장

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Transient
    private int verificationCode;

    @Transient
    private String decryptedEmail;

    @Transient
    private String decryptedPhoneNumber;

    @Transient
    private String identifier;


    public String getDecryptedEmail() {
        return Objects.requireNonNullElseGet(
                decryptedEmail,
                () -> decryptedEmail = SEEDEncoder.decrypt(email)
        );
    }

    public String getDecryptedPhoneNumber() {
        return Objects.requireNonNullElseGet(
                decryptedPhoneNumber,
                () -> decryptedPhoneNumber = SEEDEncoder.decrypt(phoneNumber)
        );
    }

    public void encryptAll(PasswordEncoder passwordEncoder) {
        encryptEmail();
        encryptPhoneNumber();
        encryptPassword(passwordEncoder);
    }

    private void encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }


    public void encryptEmail() {
        email = SEEDEncoder.encrypt(decryptedEmail);
    }


    public void encryptPhoneNumber() {
        phoneNumber = SEEDEncoder.encrypt(decryptedPhoneNumber);
    }


    public void resetPassword(String password) {
        this.password = password;
    }
}

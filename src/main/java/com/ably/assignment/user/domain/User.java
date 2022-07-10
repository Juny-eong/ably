package com.ably.assignment.user.domain;


import com.ably.assignment.global.base.BaseTimeEntity;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.user.domain.enumerated.GenderConverter;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

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
    private Long decryptedPhoneNumber;


    public String getDecryptedEmail() {
        return Objects.requireNonNullElseGet(
                decryptedEmail,
                () -> decryptedEmail = SEEDEncoder.decrypt(email)
        );
    }

    public Long getDecryptedPhoneNumber() {
        return Objects.requireNonNullElseGet(
                decryptedPhoneNumber,
                () -> decryptedPhoneNumber = Long.valueOf(SEEDEncoder.decrypt(String.valueOf(phoneNumber)))
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
        phoneNumber = SEEDEncoder.encrypt(String.valueOf(decryptedPhoneNumber));
    }


}

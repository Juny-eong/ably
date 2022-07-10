package com.ably.assignment.user.domain;


import com.ably.assignment.global.base.BaseTimeEntity;
import com.ably.assignment.global.encrypt.SEEDEncoder;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.user.domain.enumerated.GenderConverter;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private Long phoneNumber;

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

    public void encryptAll() {
        encryptEmail();
        encryptPhoneNumber();
    }


    public void encryptEmail() {
        email = SEEDEncoder.encrypt(email);
    }


    public void encryptPhoneNumber() {
        phoneNumber = Long.valueOf(SEEDEncoder.encrypt(String.valueOf(phoneNumber)));
    }


}

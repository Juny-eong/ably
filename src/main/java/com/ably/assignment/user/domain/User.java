package com.ably.assignment.user.domain;


import com.ably.assignment.global.base.BaseTimeEntity;
import com.ably.assignment.user.domain.enumerated.Gender;
import com.ably.assignment.user.domain.enumerated.GenderConverter;
import lombok.*;

import javax.persistence.*;

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


}

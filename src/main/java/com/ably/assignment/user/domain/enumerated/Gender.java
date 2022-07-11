package com.ably.assignment.user.domain.enumerated;

import com.ably.assignment.global.error.ErrorCode;
import com.ably.assignment.global.error.exception.ConversionFailedException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("M"), FEMALE("F");

    private final String value;

    @JsonCreator
    public Gender of(String valueFromRequest) {
        return Arrays.stream(Gender.values())
                .filter(e -> e.toString().equalsIgnoreCase(valueFromRequest))
                .findFirst()
                .orElseThrow(() -> new ConversionFailedException(ErrorCode.INVALID_GENDER_TYPE));
    }

    @JsonValue
    public String toLowerCaseString() {
        return this.toString().toLowerCase();
    }
}

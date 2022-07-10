package com.ably.assignment.user.domain.enumerated;

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
                .orElseThrow(RuntimeException::new); // TODO exception
    }

    @JsonValue
    public String toLowerCaseString() {
        return this.toString().toLowerCase();
    }
}

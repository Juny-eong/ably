package com.ably.assignment.user.domain.enumerated;

import com.ably.assignment.global.error.ErrorCode;
import com.ably.assignment.global.error.exception.ConversionFailedException;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return Arrays.stream(Gender.values())
                .filter(e -> e.getValue().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(() -> new ConversionFailedException(ErrorCode.INVALID_GENDER_TYPE));
    }
}

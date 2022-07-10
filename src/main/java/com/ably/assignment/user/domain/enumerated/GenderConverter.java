package com.ably.assignment.user.domain.enumerated;

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
                .orElseThrow(RuntimeException::new); // TODO exception
    }
}

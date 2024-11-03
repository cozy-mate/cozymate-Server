package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TodoTypeConverter implements AttributeConverter<TodoType, String> {

    @Override
    public String convertToDatabaseColumn(TodoType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public TodoType convertToEntityAttribute(String dbData) {
        return dbData != null ? TodoType.valueOf(dbData) : null;
    }
}
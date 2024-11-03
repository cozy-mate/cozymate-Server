package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TodoTypeConverter implements AttributeConverter<TodoType, String> {

    @Override
    public String convertToDatabaseColumn(TodoType type) {
        return type != null ? type.name() : null;
    }

    @Override
    public TodoType convertToEntityAttribute(String data) {
        return data != null ? TodoType.valueOf(data) : null;
    }
}
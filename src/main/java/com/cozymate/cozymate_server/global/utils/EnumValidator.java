package com.cozymate.cozymate_server.global.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumValid, String> {

    private EnumValid annotation;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        String className = extractClassName(enumClass);

        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(className + "의 enum 값이 null일 수 없습니다.")
                .addConstraintViolation();
            return false;
        }

        Object[] enumValues = this.annotation.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                if (value.equalsIgnoreCase(enumValue.toString())) {
                    return true;
                }
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(className + "의 enum 값이 잘못되었습니다.")
            .addConstraintViolation();
        return false;
    }

    private String extractClassName(Class<? extends Enum<?>> enumClass) {
        String classPath = enumClass.getName();
        int index = classPath.lastIndexOf('.') + 1;
        return (index != -1) ? classPath.substring(index) : classPath;
    }
}
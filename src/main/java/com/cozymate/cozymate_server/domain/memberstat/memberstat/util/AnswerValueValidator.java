package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

public class AnswerValueValidator implements ConstraintValidator<AnswerValueValid, Object> {

    private String questionKey;

    @Override
    public void initialize(AnswerValueValid constraintAnnotation) {
        this.questionKey = constraintAnnotation.questionKey();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        List<String> allowedValues = QuestionAnswerMapper.getOptions(questionKey);

        if (value instanceof String strValue) {
            if (!strValue.isBlank() && !allowedValues.contains(strValue)) {
                setCustomMessage(context, questionKey, strValue);
                return false;
            }
            return true;
        }

        if (value instanceof List<?> listValue) {
            List<String> invalidValues = listValue.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(val -> !allowedValues.contains(val))
                .toList();

            if (!invalidValues.isEmpty()) {
                setCustomMessage(context, questionKey, invalidValues.toString());
                return false;
            }
            return true;
        }

        setCustomMessage(context, questionKey, "지원되지 않는 타입");
        return false;
    }

    private void setCustomMessage(ConstraintValidatorContext context, String field, String invalidValue) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
            String.format("[%s] 필드에 유효하지 않은 값이 입력되었습니다: %s", field, invalidValue)
        ).addConstraintViolation();
    }
}

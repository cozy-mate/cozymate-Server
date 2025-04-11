package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AnswerValueValidator.class})
public @interface AnswerValueValid {
    String message() default "허용되지 않는 값입니다.";
    String questionKey(); // JSON의 key 이름
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

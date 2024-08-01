package com.cozymate.cozymate_server.global.utils;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerApiError {

    ErrorStatus[] value();
}
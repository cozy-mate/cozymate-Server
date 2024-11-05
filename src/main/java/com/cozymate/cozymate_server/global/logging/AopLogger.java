package com.cozymate.cozymate_server.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AopLogger {

    @Pointcut("execution(* com.cozymate.cozymate_server.domain..*.*(..)) && !within(com.cozymate.cozymate_server.domain.auth..*)")
    public void classAdvice() {
    }

    @Before("classAdvice()")
    public void beforeClassCall(JoinPoint joinPoint) {
        if(MDC.get("startTime") != null){
            long startTime = Long.parseLong(MDC.get("startTime"));
            long endTime = System.currentTimeMillis();
            String functionName = joinPoint.getSignature().getName();
            log.info("[FUNCION] REQUIESID: {}, FUNC: {}, TIME: {}ms",
                MDC.get("requestId"), functionName, endTime - startTime
            );
        }

    }
}

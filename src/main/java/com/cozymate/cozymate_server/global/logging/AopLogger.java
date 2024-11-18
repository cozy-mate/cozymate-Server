package com.cozymate.cozymate_server.global.logging;

import com.cozymate.cozymate_server.global.logging.enums.MdcKey;
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
        if (MDC.get(MdcKey.START_TIME_MILLIS.name()) != null) {
            long startTime = Long.parseLong(MDC.get(MdcKey.START_TIME_MILLIS.name()));
            long endTime = System.currentTimeMillis();
            String functionName = joinPoint.getSignature().getName();
            log.info("[FUNCTION] rid {} | func {} | time {}ms",
                MDC.get(MdcKey.REQUEST_ID.name()), functionName, endTime - startTime
            ); // RequestId | FunctionName | Time
        }
    }

    @Pointcut("execution(* com.cozymate.cozymate_server.domain..repository.*.*(..))")
    public void repositoryAdvice() {
    }

    @Before("repositoryAdvice()")
    public void beforeRepositoryCall(JoinPoint joinPoint) {
        String queryCountStr = MDC.get(MdcKey.QUERY_COUNT.name());
        int queryCount = 0; // 기본값 설정

        if (queryCountStr != null) {
                queryCount = Integer.parseInt(queryCountStr);
        }

        // QUERY_COUNT 증가 및 MDC 업데이트
        MDC.put(MdcKey.QUERY_COUNT.name(), String.valueOf(queryCount + 1));
    }

}

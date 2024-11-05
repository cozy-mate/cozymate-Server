package com.cozymate.cozymate_server.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MdcLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        // TODO: SessionId
        MDC.put("startTime", String.valueOf(System.currentTimeMillis()));
        MDC.put("requestId", java.util.UUID.randomUUID().toString());
        log.info("REQUESTID: {}, IP: {}, METHOD: {}, URI: {}, PARAMS: {}",
            MDC.get("requestId"),
            request.getRemoteAddr(),
            request.getMethod(),
            request.getRequestURI(),
            request.getQueryString());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {

        if (MDC.get("startTime") != null && MDC.get("requestId") != null) {
            long startTime = Long.parseLong(MDC.get("startTime"));
            long endTime = System.currentTimeMillis();
            log.info(
                "REQUESTID: {}, IP: {}, METHOD: {}, URI: {}, STATUS: {}, TIME: {}ms",
                MDC.get("requestId"),
                request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                endTime - startTime
            );
        }
        MDC.clear();
    }
}

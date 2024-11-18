package com.cozymate.cozymate_server.global.logging;

import com.cozymate.cozymate_server.global.logging.enums.MdcKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MdcLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        if(request.getHeader("Authorization") == null) {
            return true;
        }

        setMdc(request);

        log.info("[ REQUEST] rid {} | token {} | ip {} | method {} | uri {} | param {} | path {}",
            MDC.get(MdcKey.REQUEST_ID.name()),
            request.getHeader("Authorization").split(" ")[1].split("\\.")[1],
            MDC.get(MdcKey.REQUEST_IP.name()),
            MDC.get(MdcKey.REQUEST_METHOD.name()),
            MDC.get(MdcKey.REQUEST_URI.name()),
            MDC.get(MdcKey.REQUEST_PARAMS.name()),
            extractPathVariables(request.getRequestURI())
        );

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {

        if (MDC.get(MdcKey.START_TIME_MILLIS.name()) != null
            && MDC.get(MdcKey.REQUEST_ID.name()) != null) {
            long startTime = Long.parseLong(MDC.get(MdcKey.START_TIME_MILLIS.name()));
            long endTime = System.currentTimeMillis();
            log.info(
                "[RESPONSE] {} | {} | {} | {} | {}ms | {} | {} | {}",
                MDC.get(MdcKey.REQUEST_ID.name()),
                MDC.get(MdcKey.REQUEST_METHOD.name()),
                MDC.get(MdcKey.REQUEST_URI.name()),
                response.getStatus(),
                endTime - startTime,
                MDC.get(MdcKey.REQUEST_IP.name()),
                MDC.get(MdcKey.REQEUST_AGENT.name()),
                MDC.get(MdcKey.QUERY_COUNT.name())
            );
            // RequestId | Method | URI | Status | Time | IP | User-Agent | QueryCount
        }
        MDC.clear();
    }

    private void setMdc(HttpServletRequest request) {
        MDC.put(MdcKey.REQUEST_ID.name(), UUID.randomUUID().toString());
        MDC.put(MdcKey.REQUEST_IP.name(), request.getRemoteAddr());
        MDC.put(MdcKey.REQUEST_METHOD.name(), request.getMethod());
        MDC.put(MdcKey.REQUEST_URI.name(), request.getRequestURI().replaceAll("\\d+", "{id}"));
        MDC.put(MdcKey.REQUEST_PARAMS.name(), request.getQueryString());
        MDC.put(MdcKey.REQUEST_TIME.name(), LocalDateTime.now().toString());
        MDC.put(MdcKey.START_TIME_MILLIS.name(), String.valueOf(System.currentTimeMillis()));
        MDC.put(MdcKey.REQEUST_AGENT.name(), extractAgent(request.getHeader("User-Agent")));
        MDC.put(MdcKey.QUERY_COUNT.name(), "0");
    }

    private String extractAgent(String agent) {
        if (agent != null) {
            agent = agent.toLowerCase();

            if (agent.contains("mobile")) { // 모바일 여부 확인
                if (agent.contains("android")) {
                    return "android";
                } else if (agent.contains("iphone") || agent.contains("ipad") || agent.contains(
                    "ios")) {
                    return "ios";
                } else {
                    return "unknown-mobile";
                }
            } else {
                return "pc";
            }
        } else {
            return agent; // User-Agent가 없는 경우
        }
    }

    private String extractPathVariables(String requestUri) {
        // 정규식으로 숫자 패턴 추출
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(requestUri);

        List<String> pathVariables = new ArrayList<>();
        while (matcher.find()) {
            pathVariables.add(matcher.group());
        }

        return pathVariables.toString();
    }
}

package com.cozymate.cozymate_server.global.response.exception;

import com.cozymate.cozymate_server.global.logging.enums.MdcKey;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.ErrorReasonDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // Bean Validation 제약 조건 위반 시 발생하는 예외를 처리
    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return handleExceptionInternalConstraint(e, ErrorStatus.valueOf(errorMessage),
            HttpHeaders.EMPTY, request);
    }

    // @Valid 어노테이션을 통한 검증 실패 시 발생하는 예외를 처리
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status,
        WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors()
            .forEach(fieldError -> {
                String fieldName = fieldError.getField();
                String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage())
                    .orElse("");
                errors.merge(fieldName, errorMessage,
                    (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", "
                        + newErrorMessage);
            });

        return handleExceptionInternalArgs(e, HttpHeaders.EMPTY,
            ErrorStatus.valueOf("_BAD_REQUEST"), request, errors);
    }

    // 모든 Exception 클래스 타입의 예외 처리
    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {

        log.error("[   ERROR] rid {} | msg {} | loc {}", MDC.get(MdcKey.REQUEST_ID.name()),
            e.getMessage(), e.getStackTrace()[0]);
        Sentry.withScope(scope -> {
            scope.setTag("handled", "no");
            scope.setTag("status_code",
                ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus().toString());
            scope.setTag("user_id", MDC.get(MdcKey.USER_ID.name()));
            scope.setExtra("location", e.getStackTrace()[0].toString());
            scope.setExtra("message", e.getMessage());
            scope.setLevel(SentryLevel.FATAL);
            scope.setFingerprint(List.of("cozymate", "FATAL", MDC.get(MdcKey.USER_ID.name()),
                MDC.get(MdcKey.REQUEST_ID.name())));
            Sentry.captureException(e);
        });

        return handleExceptionInternalFalse(e, ErrorStatus._INTERNAL_SERVER_ERROR,
            HttpHeaders.EMPTY, ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(), request,
            e.getMessage());
    }

    // 사용자 정의 예외 처리
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity onThrowException(GeneralException generalException,
        HttpServletRequest request) {

        Sentry.withScope(scope -> {
            GeneralException newGeneralException = new GeneralException(
                generalException.getErrorReasonHttpStatus().getMessage(), // 메시지
                generalException.getCode() // 코드
            );

            scope.setTag("handled", "yes");
            scope.setTag("status_code", generalException.getErrorReasonHttpStatus().getCode());
            scope.setTag("user_id", MDC.get(MdcKey.USER_ID.name()));
            scope.setExtra("message", generalException.getErrorReasonHttpStatus().getMessage());
            scope.setLevel(SentryLevel.WARNING);
            scope.setFingerprint(List.of("cozymate", "WARN", MDC.get(MdcKey.USER_ID.name()),
                generalException.getErrorReasonHttpStatus().getCode()));

            Sentry.captureException(newGeneralException);
        });

        ErrorReasonDto errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorReasonDto reason,
        HttpHeaders headers, HttpServletRequest request) {

        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(),
            null);
        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            reason.getHttpStatus(),
            webRequest
        );
    }


    // 공통 예외 처리 메소드
    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e,
        ErrorStatus errorCommonStatus,
        HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
            errorCommonStatus.getMessage(), errorPoint);
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            status,
            request
        );
    }

    // 서버 에러 처리 메소드
    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers,
        ErrorStatus errorCommonStatus,
        WebRequest request, Map<String, String> errorArgs) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
            errorCommonStatus.getMessage(), errorArgs);
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            errorCommonStatus.getHttpStatus(),
            request
        );
    }

    // 검증 실패에 대한 처리 메소드
    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e,
        ErrorStatus errorCommonStatus,
        HttpHeaders headers, WebRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
            errorCommonStatus.getMessage(), null);
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            errorCommonStatus.getHttpStatus(),
            request
        );
    }
}
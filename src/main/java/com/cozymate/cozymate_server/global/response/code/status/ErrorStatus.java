package com.cozymate.cozymate_server.global.response.code.status;

import com.cozymate.cozymate_server.global.response.code.BaseErrorCode;
import com.cozymate.cozymate_server.global.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 일반 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // [도메인] 관련해서 아래에 계속 추가해주시면 됩니다.

    // Member 관련 에러
    _MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER400", "멤버를 찾을 수 없습니다."),

    // ChatRoom 관련 애러
    _CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHATROOM400", "쪽지방을 찾을 수 없습니다."),
    _CHATROOM_FORBIDDEN(HttpStatus.BAD_REQUEST,"CHATROOM401", "해당 쪽지방을 삭제할 권한이 없습니다."),

    // Chat 관련 에러
    _CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT400", "쪽지를 찾을 수 없습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .build();
    }
}
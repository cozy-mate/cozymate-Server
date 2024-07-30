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

    // S3 관련
    _FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "FILE_001", "파일 업로드에 실패했습니다."),
    _FILE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "FILE_002", "파일 삭제에 실패했습니다."),
    _FILE_EXTENSTION_ERROR(HttpStatus.BAD_REQUEST, "FILE_003", "유효하지 않은 파일 확장자입니다."),

    // Room
    _ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM400", "존재하지 않는 방입니다."),
    _ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROOM409", "이미 활성화 또는 대기 중인 방이 존재합니다."),

    // University 관련 에러
    _UNIVERSITY_NOT_FOUND(HttpStatus.BAD_REQUEST,"UNIVERSITY400","대학을 찾을 수 없습니다."),

    // MemberStat 관련 에러
    _MEMBERSTAT_EXISTS(HttpStatus.BAD_REQUEST,"MEMBERSTAT400","멤버 상세정보가 이미 존재합니다."),
    _MEMBERSTAT_MERIDIAN_NOT_VALID(HttpStatus.BAD_REQUEST,"MEMBERSTAT401","오전, 오후를 정확하게 입력하세요."),

    // Mate 관련
    _MATE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATE400", "해당하는 메이트 정보가 없습니다."),

    // Todo 관련
    _TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "TODO400", "해당하는 Todo 정보가 없습니다."),
    _TODO_NOT_VALID(HttpStatus.BAD_REQUEST, "TODO401", "수정할 수 있는 권한이 없습니다.")

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
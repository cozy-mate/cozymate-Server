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
    // 소셜
    // 카카오

    _KAKAO_ACCESS_RESPONSE_RECEIVING_FAIL(
            HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500", "카카오에서 response를 받지 못했습니다."),

    _KAKAO_RESPONSE_PARSING_FAIL(
            HttpStatus.INTERNAL_SERVER_ERROR, "AUTH501", "카카오 response 파싱 실패"),
    _KAKAO_ACCESS_TOKEN_PARSING_FAIL(
            HttpStatus.INTERNAL_SERVER_ERROR, "AUTH501", "카카오 accessToken 파싱 실패"),



    // [Member] 관련 에러
    _MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER400", "멤버를 찾을 수 없습니다."),

    // Token
    _TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN400", "사용자의 리프레시 토큰을 찾을 수 없습니다."),

    // S3 관련
    _FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "FILE_001", "파일 업로드에 실패했습니다."),
    _FILE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "FILE_002", "파일 삭제에 실패했습니다."),
    _FILE_EXTENSTION_ERROR(HttpStatus.BAD_REQUEST, "FILE_003", "유효하지 않은 파일 확장자입니다."),

    // Room
    _ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM400", "존재하지 않는 방입니다."),
    _ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROOM401", "이미 활성화 또는 대기 중인 방이 존재합니다."),
    _ROOM_MANAGER_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM402", "방장이 존재하지 않습니다."),
    _NOT_ROOM_MANAGER(HttpStatus.BAD_REQUEST, "ROOM403", "방장이 아닙니다."),
    _NOT_ROOM_MATE(HttpStatus.BAD_REQUEST, "ROOM404", "해당 방의 룸메이트가 아닙니다."),
    _ALREADY_JOINED_ROOM(HttpStatus.BAD_REQUEST, "ROOM405", "이미 참가한 방입니다."),
    _ROOM_FULL(HttpStatus.BAD_REQUEST, "ROOM406", "방 정원이 꽉 찼습니다."),

    // University 관련 에러
    _UNIVERSITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "UNIVERSITY400", "대학을 찾을 수 없습니다."),

    // MemberStat 관련 에러
    _MEMBERSTAT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTAT400", "멤버 상세정보가 이미 존재합니다."),
    _MEMBERSTAT_MERIDIAN_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT401", "오전, 오후를 정확하게 입력하세요."),
    _MEMBERSTAT_NOT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTAT402", "멤버 상세정보가 존재하지 않습니다."),
    _MEMBERSTAT_FILTER_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST,"MEMBERSTAT403", "멤버 상세정보 filterList이 잘못되었습니다."),

    // ChatRoom 관련 애러
    _CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHATROOM400", "쪽지방을 찾을 수 없습니다."),
    _CHATROOM_FORBIDDEN(HttpStatus.BAD_REQUEST,"CHATROOM401", "해당 쪽지방을 삭제할 권한이 없습니다."),
    _CHATROOM_MEMBER_MISMATCH(HttpStatus.BAD_REQUEST, "CHATROOM402", "해당 쪽지방의 멤버가 아닙니다."),

    // Mate 관련
    _MATE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATE400", "해당하는 메이트 정보가 없습니다."),

    // Chat 관련 에러
    _CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT400", "쪽지를 찾을 수 없습니다."),

    // Todo 관련
    _TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "TODO400", "해당하는 Todo 정보가 없습니다."),
    _TODO_NOT_VALID(HttpStatus.BAD_REQUEST, "TODO401", "수정할 수 있는 권한이 없습니다."),

    // Friend 관련 에러
    _FRIEND_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "FRIEND400", "친구요청을 찾을 수 없습니다."),
    _FRIEND_REQUEST_SENT(HttpStatus.BAD_REQUEST, "FRIEND401", "보낸 친구요청입니다."),
    _FRIEND_REQUEST_RECEIVED(HttpStatus.BAD_REQUEST, "FRIEND402", "받은 친구요청입니다."),
    _FRIEND_REQUEST_ACCEPTED(HttpStatus.BAD_REQUEST, "FRIEND403", "이미 수락한 친구요청입니다."),
    _FRIEND_REQUEST_WAITING(HttpStatus.BAD_REQUEST, "FRIEND404", "대기 중인 친구요청입니다."),

    // Role 관련
    _ROLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROLE400", "역할을 찾을 수 없습니다."),
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
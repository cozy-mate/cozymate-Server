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


    // [Member] 관련 에러
    _MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER400", "멤버를 찾을 수 없습니다."),
    _MEMBER_BINDING_FAIL(HttpStatus.BAD_REQUEST, "MEMBER401", "회원가입 요청 바인딩 실패"),
    _MEMBER_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "MEMBER403", "메일 인증을 완료해주세요"),
    _MEMBER_EXISTING(HttpStatus.BAD_REQUEST, "MEMBER402", "이미 존재하는 사용자 입니다"),

    _INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "SOCIAL400", "제공하지 않는 소셜 타입입니다."),
    _INVALID_GENDER(HttpStatus.BAD_REQUEST, "GENDER400", "제공하지 않는 성별입니다."),

    // Token
    _TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN400", "사용자의 리프레시 토큰을 찾을 수 없습니다."),
    _TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOKEN401", "토큰이 유효하지 않습니다."),
    _TEMPORARY_TOKEN_ACCESS_DENIED_(HttpStatus.BAD_REQUEST, "TOKEN402", "임시토큰으로 접근 할 수 없습니다."),
    _REFRESH_TOKEN_ACCESS_DENIED_(HttpStatus.BAD_REQUEST, "TOKEN403", "refresh 토큰으로 접근 할 수 없습니다."),


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
    _ROOM_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "ROOM405", "이미 참가한 방입니다."),
    _ROOM_FULL(HttpStatus.BAD_REQUEST, "ROOM406", "방 정원이 꽉 찼습니다."),
    _ROOM_WAITING(HttpStatus.BAD_REQUEST,"ROOM407","대기 중인 방입니다."),
    _INVITATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM408", "존재하지 않는 초대요청입니다."),
    _INVITATION_ALREADY_SENT(HttpStatus.BAD_REQUEST, "ROOM409", "이미 초대되었습니다."),
    _CANNOT_SELF_FORCED_QUIT(HttpStatus.BAD_REQUEST, "ROOM410", "자신을 강제퇴장시킬 수 없습니다."),
    _REQUEST_ALREADY_SENT(HttpStatus.BAD_REQUEST, "ROOM411", "이미 참여요청 되었습니다."),
    _REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM412", "존재하지 않는 참여요청입니다."),
    _PUBLIC_ROOM(HttpStatus.BAD_REQUEST, "ROOM413", "공개방입니다."),

    // Hashtag
    _DUPLICATE_HASHTAGS(HttpStatus.BAD_REQUEST, "HASHTAG400", "중복된 해시태그는 입력할 수 없습니다."),

    // University 관련 에러
    _UNIVERSITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "UNIVERSITY400", "대학을 찾을 수 없습니다."),
    _UNIVERSITY_BINDING_FAIL(HttpStatus.BAD_REQUEST, "UNIVERSITY401", "대학교 바인딩 실패"),

    // MemberStat 관련 에러
    _MEMBERSTAT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTAT400", "멤버 상세정보가 이미 존재합니다."),
    _MEMBERSTAT_MERIDIAN_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT401", "오전, 오후를 정확하게 입력하세요."),
    _MEMBERSTAT_NOT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTAT402", "멤버 상세정보가 존재하지 않습니다."),
    _MEMBERSTAT_FILTER_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT403",
        "멤버 상세정보 filterList이 잘못되었습니다."),
    _MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE(HttpStatus.BAD_REQUEST, "MEMBERSTAT404", "인실이 정해진 경우 인실 필터링이 불가합니다."),
    _MEMBERSTAT_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT405",
        "멤버 상세정보 인자가 잘못되었습니다."),
    _MEMBERSTAT_NEEDS_DETAIL_NEEDS_PREFERENCES_CANNOT_COEXIST(HttpStatus.BAD_REQUEST, "MEMBERSTAT406",
        "needsDetail 옵션과 needsPreferences 옵션은 공존할 수 없습니다."),


    _MEMBERSTAT_PREFERENCE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTATPREFERENCE400", "멤버 선호 항목이 존재하지 않습니다."),
    _MEMBERSTAT_PREFERENCE_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST,"MEMBERSTATPREFERENCE401","존재하지 않는 멤버 항목(들)입니다."),
    // ChatRoom 관련 애러
    _CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHATROOM400", "쪽지방을 찾을 수 없습니다."),
    _CHATROOM_FORBIDDEN(HttpStatus.BAD_REQUEST, "CHATROOM401", "해당 쪽지방을 삭제할 권한이 없습니다."),
    _CHATROOM_MEMBER_MISMATCH(HttpStatus.BAD_REQUEST, "CHATROOM402", "해당 쪽지방의 멤버가 아닙니다."),

    // Mate 관련
    _MATE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATE400", "해당하는 메이트 정보가 없습니다."),
    _MATE_OR_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATE401", "해당하는 메이트나 방 정보가 없습니다."),

    // Chat 관련 에러
    _CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT400", "쪽지를 찾을 수 없습니다."),
    _CHAT_NOT_FOUND_RECIPIENT(HttpStatus.BAD_REQUEST, "CHAT401", "존재하지 않는 수신인입니다."),

    // Rule 관련
    _RULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "RULE400", "해당하는 Rule이 없습니다."),
    _RULE_MATE_MISMATCH(HttpStatus.BAD_REQUEST, "RULE401", "Mate에게 해당 Rule을 삭제할 권한이 없습니다."),
    _RULE_OVER_MAX(HttpStatus.BAD_REQUEST, "RULE402", "생성할 수 있는 Rule 개수를 초과했습니다."),

    // 투두 관련
    _TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "TODO400", "해당하는 Todo 정보가 없습니다."),
    _TODO_NOT_VALID(HttpStatus.BAD_REQUEST, "TODO401", "수정할 수 있는 권한이 없습니다."),
    _TODO_OVER_MAX(HttpStatus.BAD_REQUEST, "TODO402", "생성할 수 있는 하루 최대 Todo 개수를 초과했습니다."),
    _TODO_NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "TODO403", "해당하는 방에 해당 Todo가 없습니다."),
    _TODO_NOT_DELETE(HttpStatus.BAD_REQUEST, "TODO404", "삭제할 수 있는 권한이 없습니다."),

    // Friend 관련 에러
    _FRIEND_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "FRIEND400", "친구요청을 찾을 수 없습니다."),
    _FRIEND_REQUEST_SENT(HttpStatus.BAD_REQUEST, "FRIEND401", "보낸 친구요청입니다."),
    _FRIEND_REQUEST_RECEIVED(HttpStatus.BAD_REQUEST, "FRIEND402", "받은 친구요청입니다."),
    _FRIEND_REQUEST_ACCEPTED(HttpStatus.BAD_REQUEST, "FRIEND403", "이미 수락한 친구요청입니다."),
    _FRIEND_REQUEST_WAITING(HttpStatus.BAD_REQUEST, "FRIEND404", "대기 중인 친구요청입니다."),
    _FRIEND_REQUEST_EQUAL(HttpStatus.BAD_REQUEST, "FRIEND405", "같은 사람에게 친구요청을 보낼 수 없습니다."),
    _NOT_FRIEND(HttpStatus.BAD_REQUEST, "FRIEND406", "친구가 아닙니다."),

    // Role 관련
    _ROLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROLE400", "역할을 찾을 수 없습니다."),
    _ROLE_NOT_VALID(HttpStatus.BAD_REQUEST, "ROLE401", "Role을 수정할 권한이 없습니다."),

    // Feed 관련 에러
    _FEED_EXISTS(HttpStatus.BAD_REQUEST, "FEED400", "피드 정보가 이미 존재합니다."),
    _FEED_NOT_EXISTS(HttpStatus.BAD_REQUEST, "FEED401", "피드 정보가 존재하지 않습니다."),

    // Post관련
    _POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST400", "게시물이 존재하지 않습니다."),

    // Post Comment
    _POST_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT400", "댓글이 존재하지 않습니다."),

    // Mail
    _MAIL_AUTHENTICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "MAIL400","메일인증을 받아주세요."),
    _MAIL_ADDRESS_DUPLICATED(HttpStatus.BAD_REQUEST,"MAIL401","이미 사용된 메일입니다."),
    _MAIL_AUTHENTICATION_CODE_INCORRECT(HttpStatus.BAD_REQUEST,"MAIL402","인증 코드가 올바르지 않습니다."),
    _MAIL_AUTHENTICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST,"MAIL403","메일 인증코드가 만료되었습니다. 다시 받아주세요"),
    _INVALID_MAIL_ADDRESS_DOMAIN(HttpStatus.BAD_REQUEST,"MAIL404","메일 도메인이 올바르지 않습니다."),

    // MemberBlock 관련
    _ALREADY_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBERBLOCK400", "이미 차단된 사용자입니다."),
    _CANNOT_BLOCK_REQUEST_SELF(HttpStatus.BAD_REQUEST, "MEMBERBLOCK401", "자신에 대해 차단 관련 요청을 할 수 없습니다."),
    _ALREADY_NOT_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBERBLOCK402", "이미 차단되지 않은 사용자입니다."),
    _REQUEST_TO_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBERBLOCK403", "차단한 사용자에 대한 요청입니다."),

    // MemberStatEquality 관련
    _MEMBERSTAT_EQUALITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBERSTATEQUALITY400", "일치율이 존재하지 않습니다."),

    // Report 관련
    _REPORT_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "REPORT400", "신고 대상 멤버를 찾을 수 없습니다."),
    _REPORT_DUPLICATE(HttpStatus.BAD_REQUEST, "REPORT401", "중복된 신고 요청입니다."),
    _REPORT_CANNOT_REQUEST_SELF(HttpStatus.BAD_REQUEST, "REPORT402", "자신에 대한 차단 관련 요청을 할 수 없습니다."),

    // Favorite 관련
    _FAVORITE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FAVORITE400", "이미 찜이 되어 있습니다."),
    _FAVORITE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FAVORITE401", "찜을 찾을 수 없습니다."),
    _FAVORITE_MEMBER_MISMATCH(HttpStatus.BAD_REQUEST, "FAVORITE402", "해당 찜에 대한 권한이 없습니다."),
    _FAVORITE_CANNOT_REQUEST_SELF(HttpStatus.BAD_REQUEST, "FAVORITE403", "자신에 대한 찜 관련 요청을 할 수 없습니다."),
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
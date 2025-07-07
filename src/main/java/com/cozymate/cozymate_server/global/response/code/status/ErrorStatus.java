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

    _NICKNAME_EXISTING(HttpStatus.BAD_REQUEST, "MEMBER404", "이미 존재하는 닉네임 입니다"),
    _INVALID_NICKNAME_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER405", "한글, 영어로 시작해야합니다. : "
        + "첫 번째 문자는 한글 또는 영문 대소문자, 이후에는 한글, 영문 대소문자, 숫자, _(underscore)만 허용"),
    _INVALID_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "MEMBER406", "닉네임 길이는 2에서 8사이입니다."),


    _INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "SOCIAL400", "제공하지 않는 소셜 타입입니다."),
    _INVALID_GENDER(HttpStatus.BAD_REQUEST, "GENDER400", "제공하지 않는 성별입니다."),

    // Token
    _TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN400", "사용자의 리프레시 토큰을 찾을 수 없습니다."),
    _TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOKEN401", "토큰이 유효하지 않습니다."),
    _TEMPORARY_TOKEN_NO_USER_ACCESS_DENIED_(HttpStatus.BAD_REQUEST, "TOKEN402", "임시토큰(회원아님)으로 접근 할 수 없습니다."),
    _REFRESH_TOKEN_ACCESS_DENIED_(HttpStatus.BAD_REQUEST, "TOKEN403", "refresh 토큰으로 접근 할 수 없습니다."),
    _TOKEN_AUTHORIZATION_EMPTY(HttpStatus.BAD_REQUEST, "TOKEN404", "사용자 권한이 비어있습니다."),
    _TEMPORARY_TOKEN_PRE_USER_ACCESS_DENIED_(HttpStatus.BAD_REQUEST, "TOKEN405", "임시토큰(준회원)으로 접근 할 수 없습니다."),


    // S3 관련
    _FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "FILE_001", "파일 업로드에 실패했습니다."),
    _FILE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "FILE_002", "파일 삭제에 실패했습니다."),
    _FILE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "FILE_003", "유효하지 않은 파일 확장자입니다."),

    // Room
    _ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM400", "존재하지 않는 방입니다."),
    _ROOM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROOM401", "이미 활성화 또는 대기 중인 방이 존재합니다."),
    _ROOM_MANAGER_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM402", "방장이 존재하지 않습니다."),
    _NOT_ROOM_MANAGER(HttpStatus.BAD_REQUEST, "ROOM403", "방장이 아닙니다."),
    _NOT_ROOM_MATE(HttpStatus.BAD_REQUEST, "ROOM404", "해당 방의 룸메이트가 아닙니다."),
    _ROOM_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "ROOM405", "이미 참가한 방입니다."),
    _ROOM_FULL(HttpStatus.BAD_REQUEST, "ROOM406", "방 정원이 꽉 찼습니다."),
    _ROOM_WAITING(HttpStatus.BAD_REQUEST, "ROOM407", "대기 중인 방입니다."),
    _INVITATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM408", "존재하지 않는 초대요청입니다."),
    _INVITATION_ALREADY_SENT(HttpStatus.BAD_REQUEST, "ROOM409", "이미 초대되었습니다."),
    _CANNOT_SELF_FORCED_QUIT(HttpStatus.BAD_REQUEST, "ROOM410", "자신을 강제퇴장시킬 수 없습니다."),
    _REQUEST_ALREADY_SENT(HttpStatus.BAD_REQUEST, "ROOM411", "이미 참여요청 되었습니다."),
    _REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM412", "존재하지 않는 참여요청입니다."),
    _PUBLIC_ROOM(HttpStatus.BAD_REQUEST, "ROOM413", "공개방입니다."),
    _PRIVATE_ROOM(HttpStatus.BAD_REQUEST, "ROOM414", "비공개방입니다."),
    _INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "ROOM415", "유효하지 않은 정렬 타입입니다."),
    _MISMATCH_GENDER(HttpStatus.BAD_REQUEST, "ROOM416", "일치하지 않는 성별입니다."),
    _MISMATCH_UNIVERSITY(HttpStatus.BAD_REQUEST, "ROOM417", "일치하지 않는 학교입니다."),


    // Hashtag
    _DUPLICATE_HASHTAGS(HttpStatus.BAD_REQUEST, "HASHTAG400", "중복된 해시태그는 입력할 수 없습니다."),

    // University 관련 에러
    _UNIVERSITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "UNIVERSITY400", "대학을 찾을 수 없습니다."),
    _UNIVERSITY_BINDING_FAIL(HttpStatus.BAD_REQUEST, "UNIVERSITY401", "대학교 바인딩 실패"),
    _UNIVERSITY_DEPARTMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "UNIVERSITY402", "해당 대학교에 없는 학과입니다."),


    // MemberStat 관련 에러
    _MEMBERSTAT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTAT400", "멤버 상세정보가 이미 존재합니다."),
    _MEMBERSTAT_MERIDIAN_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT401", "오전, 오후를 정확하게 입력하세요."),
    _MEMBERSTAT_NOT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTAT402", "멤버 상세정보가 존재하지 않습니다."),
    _MEMBERSTAT_FILTER_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT403",
        "멤버 상세정보 filterList이 잘못되었습니다."),
    _MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE(HttpStatus.BAD_REQUEST, "MEMBERSTAT404",
        "인실이 정해진 경우 인실 필터링이 불가합니다."),
    _MEMBERSTAT_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTAT405",
        "멤버 상세정보 인자가 잘못되었습니다."),
    _MEMBERSTAT_NEEDS_DETAIL_NEEDS_PREFERENCES_CANNOT_COEXIST(HttpStatus.BAD_REQUEST,
        "MEMBERSTAT406",
        "needsDetail 옵션과 needsPreferences 옵션은 공존할 수 없습니다."),
    _MEMBERSTAT_FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBERSTAT407",
        "member stat 질의 파일을 찾을 수 없습니다."),
    _MEMBERSTAT_FILE_READ_ERROR(HttpStatus.BAD_REQUEST, "MEMBERSTAT408",
        "member stat 질의 Json 파일 파싱 실패"),
    _MEMBERSTAT_PREFERENCE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERSTATPREFERENCE400",
        "멤버 선호 항목이 존재하지 않습니다."),
    _MEMBERSTAT_PREFERENCE_PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBERSTATPREFERENCE401",
        "존재하지 않는 멤버 항목(들)입니다."),

    // ChatRoom 관련 에러
    _CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHATROOM400", "쪽지방을 찾을 수 없습니다."),
    _CHATROOM_FORBIDDEN(HttpStatus.BAD_REQUEST, "CHATROOM401", "해당 쪽지방을 삭제할 권한이 없습니다."),
    _CHATROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL(HttpStatus.BAD_REQUEST, "CHATROOM402",
        "해당 쪽지방의 MemberA가 null이고, 현재 요청 Member가 ChatRoom의 MemberB가 아닙니다."),
    _CHATROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL(HttpStatus.BAD_REQUEST, "CHATROOM403",
        "해당 쪽지방의 MemberB가 null이고, 현재 요청 Member가 ChatRoom의 MemberA가 아닙니다."),
    _CHATROOM_INVALID_MEMBER(HttpStatus.BAD_REQUEST, "CHATROOM404",
        "해당 쪽지방의 두 Member가 모두 null이 아니고, 현재 요청 Member가 MemberA, MemberB 둘다 아닙니다."),

    // Mate 관련
    _MATE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATE400", "해당하는 메이트 정보가 없습니다."),
    _MATE_OR_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATE401", "해당하는 메이트나 방 정보가 없습니다."),
    _MATE_NOT_IN_SAME_ROOM(HttpStatus.BAD_REQUEST, "MATE402", "같은 방이 아닌 메이트가 있습니다."),

    // Chat 관련 에러
    _CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHAT400", "쪽지를 찾을 수 없습니다."),
    _CHAT_NOT_FOUND_RECIPIENT(HttpStatus.BAD_REQUEST, "CHAT401", "존재하지 않는 수신인입니다."),

    // Rule 관련
    _RULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "RULE400", "해당하는 Rule이 없습니다."),
    _RULE_PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "RULE401", "Mate에게 해당 Rule을 삭제할 권한이 없습니다."),
    _RULE_MAX_LIMIT(HttpStatus.BAD_REQUEST, "RULE402", "생성할 수 있는 Rule 개수를 초과했습니다."),

    // 투두 관련
    _TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "TODO400", "해당하는 Todo 정보가 없습니다."),
    _TODO_EDIT_PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "TODO401", "수정할 수 있는 권한이 없습니다."),
    _TODO_DAILY_LIMIT(HttpStatus.BAD_REQUEST, "TODO402", "생성할 수 있는 하루 최대 Todo 개수를 초과했습니다."),
    _TODO_NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "TODO403", "해당하는 방에 해당 Todo가 없습니다."),
    _TODO_ASSIGNED_MATE_LIMIT(HttpStatus.BAD_REQUEST, "TODO406", "하나의 Todo에 할당할 수 있는 할당자를 초과했습니다."),

    // TodoAssignment 관련
    _TODO_ASSIGNMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "TODO405", "해당하는 TodoAssignment 정보가 없습니다."),
    _TODO_ASSIGNMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "TODO406", "이미 완료된 Todo입니다."),
    _TODO_ASSIGNMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "TODO407", "이미 완료되지 않은 Todo입니다."),

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
    _ROLE_MAX_LIMIT(HttpStatus.BAD_REQUEST, "ROLE402", "Role을 생성할 수 있는 최대 개수를 초과했습니다."),

    // Feed 관련 에러
    _FEED_EXISTS(HttpStatus.BAD_REQUEST, "FEED400", "피드 정보가 이미 존재합니다."),
    _FEED_NOT_EXISTS(HttpStatus.BAD_REQUEST, "FEED401", "피드 정보가 존재하지 않습니다."),

    // Post관련
    _POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST400", "게시물이 존재하지 않습니다."),

    // Post Comment
    _POST_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT400", "댓글이 존재하지 않습니다."),

    // Mail
    _MAIL_AUTHENTICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "MAIL400", "메일인증을 받아주세요."),
    _MAIL_ADDRESS_DUPLICATED(HttpStatus.BAD_REQUEST, "MAIL401", "이미 사용된 메일입니다."),
    _MAIL_AUTHENTICATION_CODE_INCORRECT(HttpStatus.BAD_REQUEST, "MAIL402", "인증 코드가 올바르지 않습니다."),
    _MAIL_AUTHENTICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "MAIL403",
        "메일 인증코드가 만료되었습니다. 다시 받아주세요"),
    _INVALID_MAIL_ADDRESS_DOMAIN(HttpStatus.BAD_REQUEST, "MAIL404", "메일 도메인이 올바르지 않습니다."),
    _CANNOT_FIND_MAIL_FORM(HttpStatus.BAD_REQUEST, "MAIL500", "메일 템플릿 리소스를 찾을 수 없습니다."),
    _MAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "MAIL501", "메일을 보내는 데 실패 했습니다."),

    // MemberBlock 관련
    _ALREADY_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBERBLOCK400", "이미 차단된 사용자입니다."),
    _CANNOT_BLOCK_REQUEST_SELF(HttpStatus.BAD_REQUEST, "MEMBERBLOCK401",
        "자신에 대해 차단 관련 요청을 할 수 없습니다."),
    _ALREADY_NOT_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBERBLOCK402", "이미 차단되지 않은 사용자입니다."),
    _REQUEST_TO_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "MEMBERBLOCK403", "차단한 사용자에 대한 요청입니다."),

    // MemberStatEquality 관련
    _MEMBERSTAT_EQUALITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBERSTATEQUALITY400",
        "일치율이 존재하지 않습니다."),

    // Report 관련
    _REPORT_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "REPORT400", "신고 대상 멤버를 찾을 수 없습니다."),
    _REPORT_DUPLICATE(HttpStatus.BAD_REQUEST, "REPORT401", "중복된 신고 요청입니다."),
    _REPORT_CANNOT_REQUEST_SELF(HttpStatus.BAD_REQUEST, "REPORT402", "자신에 대한 차단 관련 요청을 할 수 없습니다."),

    // MemberFavorite 관련
    _MEMBERFAVORITE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBERFAVORITE400",
        "이미 찜이 되어 있는 사용자입니다."),
    _MEMBERFAVORITE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBERFAVORITE401",
        "해당 사용자에 대한 찜을 찾을 수 없습니다."),
    _MEMBERFAVORITE_MEMBER_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBERFAVORITE402",
        "해당 찜에 대한 권한이 없습니다."),
    _MEMBERFAVORITE_CANNOT_REQUEST_SELF(HttpStatus.BAD_REQUEST, "MEMBERFAVORITE403",
        "자신에 대한 찜 관련 요청을 할 수 없습니다."),
    _MEMBERFAVORITE_CANNOT_FAVORITE_MEMBER_WITHOUT_MEMBERSTAT(HttpStatus.BAD_REQUEST,
        "MEMBERFAVORITE404", "멤버 스탯이 없는 사용자를 찜할 수 없습니다."),

    // RoomFavorite 관련
    _ROOMFAVORITE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROOMFAVORITE400", "이미 찜이 되어 있는 방입니다."),
    _ROOMFAVORITE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOMFAVORITE401", "해당 방에 대한 찜을 찾을 수 없습니다."),
    _ROOMFAVORITE_MEMBER_MISMATCH(HttpStatus.BAD_REQUEST, "ROOMFAVORITE402", "해당 찜에 대한 권한이 없습니다."),
    _ROOMFAVORITE_CANNOT_PRIVATE_ROOM(HttpStatus.BAD_REQUEST, "ROOMFAVORITE403",
        "비공개 방은 찜을 할 수 없습니다."),
    _ROOMFAVORITE_CANNOT_DISABLE_ROOM(HttpStatus.BAD_REQUEST, "ROOMFAVORITE405",
        "삭제된 방은 찜을 할 수 없습니다."),

    // INQUIRY 관련
    _INQUIRY_EMAIL_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "INQUIRY400", "이메일 형식이 올바르지 않습니다."),
    _INQUIRY_NOT_FOUND(HttpStatus.BAD_REQUEST, "INQUIRY401", "해당 문의 내역이 존재하지 않습니다."),

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
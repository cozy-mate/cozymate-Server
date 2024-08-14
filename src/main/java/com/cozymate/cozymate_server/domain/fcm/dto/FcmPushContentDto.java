package com.cozymate.cozymate_server.domain.fcm.dto;

import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import lombok.Getter;

@Getter
public class FcmPushContentDto {

    private Member member;
    private String roleContent;
    private List<String> todoContents;

    private FcmPushContentDto(Member member) {
        this.member = member;
        roleContent = null;
        todoContents = null;
    }

    // 투두 내용 리스트로 전부 줘야하는 경우, 00시 투드 리스트 스케줄러
    private FcmPushContentDto(Member member, List<String> todoContents) {
        this.member = member;
        this.todoContents = todoContents;
        roleContent = null;
    }

    // 롤 내용 하나만 줘야하는 경우, 21시 미완료 Role 한개만 리마인더 스케줄러
    private FcmPushContentDto(Member member, String roleContent) {
        this.member= member;
        this.roleContent = roleContent;
        todoContents = null;
    }

    private FcmPushContentDto() {
    }

    public static FcmPushContentDto create(Member member) {
        return new FcmPushContentDto(member);
    }

    public static FcmPushContentDto create(Member member, String roleContent) {
        return new FcmPushContentDto(member, roleContent);
    }

    public static FcmPushContentDto create(Member member, List<String> todoContents) {
        return new FcmPushContentDto(member, todoContents);
    }

    // 방이 열렸어요 -> 에서 사용합니다!
    public static FcmPushContentDto create() {
        return new FcmPushContentDto();
    }
}
package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationContentDto {

    private Member member;
    private String roleContent;
    private List<String> todoContents;

    private NotificationContentDto(Member member) {
        this.member = member;
    }

    // 투두 내용 리스트로 전부 줘야하는 경우, 00시 투드 리스트 스케줄러
    private NotificationContentDto(Member member, List<String> todoContents) {
        this.member = member;
        this.todoContents = todoContents;
    }

    // 롤 내용 하나만 줘야하는 경우, 21시 미완료 Role 한개만 리마인더 스케줄러
    private NotificationContentDto(Member member, String roleContent) {
        this.member= member;
        this.roleContent = roleContent;
    }

    public static NotificationContentDto create(Member member) {
        return new NotificationContentDto(member);
    }

    public static NotificationContentDto create(Member member, String roleContent) {
        return new NotificationContentDto(member, roleContent);
    }

    public static NotificationContentDto create(Member member, List<String> todoContents) {
        return new NotificationContentDto(member, todoContents);
    }

    // 방이 열렸어요 -> 에서 사용합니다!
    public static NotificationContentDto create() {
        return new NotificationContentDto();
    }
}
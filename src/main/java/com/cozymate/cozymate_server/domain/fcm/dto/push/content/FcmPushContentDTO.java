package com.cozymate.cozymate_server.domain.fcm.dto.push.content;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;

public record FcmPushContentDTO(
    Member member,
    String content, // roleContent or messageContent
    List<String> todoContents,
    Room room
) {

    public static FcmPushContentDTO create(Member member) {
        return new FcmPushContentDTO(member, null, null, null);
    }

    // 롤 내용 하나만 줘야하는 경우, 21시 미완료 Role 한개만 리마인더 스케줄러
    // 쪽지 알림
    public static FcmPushContentDTO create(Member member, String content) {
        return new FcmPushContentDTO(member, content, null, null);
    }

    // 투두 내용 리스트로 전부 줘야하는 경우, 00시 투드 리스트 스케줄러
    public static FcmPushContentDTO create(Member member, List<String> todoContents) {
        return new FcmPushContentDTO(member, null, todoContents, null);
    }

    public static FcmPushContentDTO create(Member member, Room room) {
        return new FcmPushContentDTO(member, null, null, room);
    }
}

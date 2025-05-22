package com.cozymate.cozymate_server.domain.chat.validator;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ChatValidator {

    public void checkMemberMisMatch(Member member, ChatRoom chatRoom) {
        // ChatRoom의 MemberA가 null(탈퇴 회원)인 경우
        if (isNullMember(chatRoom.getMemberA())) {
            if (!isSameMember(member, chatRoom.getMemberB())) {
                throw new GeneralException(ErrorStatus._CHATROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL);
            }
            return;
        }

        // ChatRoom의 MemberB가 null(탈퇴 회원)인 경우
        if (isNullMember(chatRoom.getMemberB())) {
            if (!isSameMember(member, chatRoom.getMemberA())) {
                throw new GeneralException(ErrorStatus._CHATROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL);
            }
            return;
        }

        // ChatRoom의 두 Member가 모두 null(탈퇴 회원)이 아닌 경우, 현재 요청 Member가 MemberA, MemberB 둘다 아닌 경우
        if (!isSameMember(member, chatRoom.getMemberA())
            && !isSameMember(member, chatRoom.getMemberB())) {
            throw new GeneralException(ErrorStatus._CHATROOM_INVALID_MEMBER);
        }
    }

    public boolean isNullMember(Member member) {
        return Objects.isNull(member);
    }

    public boolean isSameMember(Member member, Member requestMember) {
        return member.getId().equals(requestMember.getId());
    }

    public boolean isChatRoomActive(ChatRoom chatRoom) {
        return Objects.nonNull(chatRoom.getMemberA()) && Objects.nonNull(chatRoom.getMemberB());
    }

    public boolean isDeleteAtNull(LocalDateTime deleteAt) {
        return Objects.isNull(deleteAt);
    }

    public boolean isChatCreateAtAfterDeleteAt(Chat chat, LocalDateTime deleteAt) {
        return chat.getCreatedAt().isAfter(deleteAt);
    }
}

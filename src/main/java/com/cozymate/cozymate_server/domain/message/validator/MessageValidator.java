package com.cozymate.cozymate_server.domain.message.validator;

import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class MessageValidator {

    public void checkMemberMisMatch(Member member, MessageRoom messageRoom) {
        // MessageRoom의 MemberA가 null(탈퇴 회원)인 경우
        if (isNullMember(messageRoom.getMemberA())) {
            if (!isSameMember(member, messageRoom.getMemberB())) {
                throw new GeneralException(ErrorStatus._MESSAGEROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL);
            }
            return;
        }

        // MessageRoom의 MemberB가 null(탈퇴 회원)인 경우
        if (isNullMember(messageRoom.getMemberB())) {
            if (!isSameMember(member, messageRoom.getMemberA())) {
                throw new GeneralException(ErrorStatus._MESSAGEROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL);
            }
            return;
        }

        // MessageRoom의 두 Member가 모두 null(탈퇴 회원)이 아닌 경우, 현재 요청 Member가 MemberA, MemberB 둘다 아닌 경우
        if (!isSameMember(member, messageRoom.getMemberA())
            && !isSameMember(member, messageRoom.getMemberB())) {
            throw new GeneralException(ErrorStatus._MESSAGEROOM_INVALID_MEMBER);
        }
    }

    public boolean isNullMember(Member member) {
        return Objects.isNull(member);
    }

    public boolean isSameMember(Member member, Member requestMember) {
        return member.getId().equals(requestMember.getId());
    }

    public boolean isMessageRoomActive(MessageRoom messageRoom) {
        return Objects.nonNull(messageRoom.getMemberA()) && Objects.nonNull(messageRoom.getMemberB());
    }

    public boolean isDeleteAtNull(LocalDateTime deleteAt) {
        return Objects.isNull(deleteAt);
    }

    public boolean isMessageCreateAtAfterDeleteAt(Message message, LocalDateTime deleteAt) {
        return message.getCreatedAt().isAfter(deleteAt);
    }
}

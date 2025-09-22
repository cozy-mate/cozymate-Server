package com.cozymate.cozymate_server.domain.messageroom.validator;

import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRoomValidator {

    private final MessageRepositoryService messageRepositoryService;

    public boolean isMessageNull(Message message) {
        return Objects.isNull(message);
    }

    public boolean isMessageReadable(LocalDateTime lastDeleteAt, Message message) {
        return Objects.isNull(lastDeleteAt) || message.getCreatedAt().isAfter(lastDeleteAt);
    }

    public boolean isMemberNull(Member member) {
        return Objects.isNull(member);
    }

    public boolean isAnyMemberNullInMessageRoom(MessageRoom messageRoom) {
        return Objects.isNull(messageRoom.getMemberA()) || Objects.isNull(messageRoom.getMemberB());
    }

    public boolean isBothMembersDeleteAtNotNull(LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt) {
        return Objects.nonNull(memberALastDeleteAt) && Objects.nonNull(memberBLastDeleteAt);
    }

    public boolean isDeletableHard(LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt, MessageRoom messageRoom) {
        Message message = messageRepositoryService.getLastMessageByMessageRoomOrNull(messageRoom);

        if (Objects.isNull(message)) {
            return true;
        }

        return message.getCreatedAt().isBefore(memberALastDeleteAt) && message.getCreatedAt()
            .isBefore(memberBLastDeleteAt);
    }

    public boolean isSameMember(Member member, Member requestMember) {
        return member.getId().equals(requestMember.getId());
    }

    public boolean existNewMessage(Member recipient, MessageRoom messageRoom, LocalDateTime lastSeenAt) {
        return messageRepositoryService.existNewMessage(recipient, messageRoom, lastSeenAt);
    }
}

package com.cozymate.cozymate_server.domain.chatroom.validator;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomValidator {

    private final ChatRepository chatRepository;

    public boolean isChatNull(Chat chat) {
        return Objects.isNull(chat);
    }

    public boolean isChatReadable(LocalDateTime lastDeleteAt, Chat chat) {
        return Objects.isNull(lastDeleteAt) || chat.getCreatedAt().isAfter(lastDeleteAt);
    }

    public boolean isMemberNull(Member member) {
        return Objects.isNull(member);
    }

    public boolean isAnyMemberNullInChatRoom(ChatRoom chatRoom) {
        return Objects.isNull(chatRoom.getMemberA()) || Objects.isNull(chatRoom.getMemberB());
    }

    public boolean isBothMembersDeleteAtNotNull(LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt) {
        return Objects.nonNull(memberALastDeleteAt) && Objects.nonNull(memberBLastDeleteAt);
    }

    public boolean isDeletableHard(LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt, ChatRoom chatRoom) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .map(chat -> chat.getCreatedAt().isBefore(memberALastDeleteAt) && chat.getCreatedAt()
                .isBefore(memberBLastDeleteAt))
            .orElse(true);
    }

    public boolean isSameMember(Member member, Member requestMember) {
        return member.getId().equals(requestMember.getId());
    }

    public boolean existNewChat(Member recipient, ChatRoom chatRoom, LocalDateTime lastSeenAt) {
        return chatRepository.existsBySenderAndChatRoomAndCreatedAtAfterOrLastSeenAtIsNull(
            recipient, chatRoom, lastSeenAt);
    }
}

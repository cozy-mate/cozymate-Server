package com.cozymate.cozymate_server.domain.message.repository;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRepositoryService {

    private final MessageRepository messageRepository;

    public Slice<Message> getMessageListByMessageRoom(MessageRoom messageRoom, Pageable pageable) {
        return messageRepository.findAllByMessageRoom(messageRoom, pageable);
    }

    public void createMessage(Message message) {
        messageRepository.save(message);
    }

    public void deleteMessageByMessageRoom(MessageRoom messageRoom) {
        messageRepository.deleteAllByMessageRoom(messageRoom);
    }

    public Message getLastMessageByMessageRoomOrNull(MessageRoom messageRoom) {
        return messageRepository.findTopByMessageRoomOrderByIdDesc(messageRoom)
            .orElse(null);
    }

    public boolean existNewMessage(Member member, MessageRoom messageRoom, LocalDateTime lastSeenAt) {
        return messageRepository.existsBySenderAndMessageRoomAndCreatedAtAfterOrLastSeenAtIsNull(
            member, messageRoom, lastSeenAt);
    }

    public Slice<Message> getMessageListByMessageRoomAndLastDeleteAt(MessageRoom messageRoom,
        LocalDateTime lastDeleteAt, Pageable pageable) {
        return messageRepository.findPagingByChatRoomAndLastDeleteAt(messageRoom, lastDeleteAt, pageable);
    }
}

package com.cozymate.cozymate_server.domain.messageroom.repository;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRoomRepositoryService {

    private final MessageRoomRepository messageRoomRepository;

    public MessageRoom getMessageRoomByIdOrThrow(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MESSAGEROOM_NOT_FOUND));
    }

    public Optional<MessageRoom> getMessageRoomByMemberAAndMemberBOptional(Member memberA,
        Member memberB) {
        return messageRoomRepository.findByMemberAAndMemberB(memberA, memberB);
    }

    public MessageRoom createMessageRoom(MessageRoom messageRoom) {
        return messageRoomRepository.save(messageRoom);
    }

    public void deleteMessageRoom(MessageRoom messageRoom) {
        messageRoomRepository.delete(messageRoom);
    }

    public List<MessageRoom> getMessageRoomListByMember(Member member) {
        return messageRoomRepository.findAllByMember(member);
    }

    public Slice<Tuple> getPagingMessageRoomListByMember(Member member, Pageable pageable) {
        return messageRoomRepository.findPagingByMember(member, pageable);
    }
}

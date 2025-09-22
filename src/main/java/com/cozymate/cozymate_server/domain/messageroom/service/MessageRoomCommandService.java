package com.cozymate.cozymate_server.domain.messageroom.service;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.converter.MessageRoomConverter;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.validator.MessageRoomValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageRoomCommandService {

    private final MessageRoomRepositoryService messageRoomRepositoryService;
    private final MessageRepositoryService messageRepositoryService;
    private final MessageRoomValidator messageRoomValidator;

    public void deleteMessageRoom(Member member, Long messageRoomId) {
        MessageRoom messageRoom = messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoomId);

        softDeleteMessageRoom(messageRoom, member);

        tryHardDeleteMessageRoom(messageRoom);
    }

    public MessageRoomIdResponseDTO saveMessageRoom(Member member, Member recipient) {
        MessageRoom messageRoom = MessageRoomConverter.toEntity(member, recipient);

        messageRoom = messageRoomRepositoryService.createMessageRoom(messageRoom);

        return MessageRoomConverter.toMessageRoomIdResponseDTO(messageRoom.getId());
    }

    private void softDeleteMessageRoom(MessageRoom messageRoom, Member member) {
        if (!messageRoomValidator.isMemberNull(messageRoom.getMemberA())
            && messageRoomValidator.isSameMember(messageRoom.getMemberA(), member)) {
            messageRoom.updateMemberALastDeleteAt(LocalDateTime.now());
            return;
        }

        if (!messageRoomValidator.isMemberNull(messageRoom.getMemberB())
            && messageRoomValidator.isSameMember(messageRoom.getMemberB(), member)) {
            messageRoom.updateMemberBLastDeleteAt(LocalDateTime.now());
            return;
        }

        throw new GeneralException(ErrorStatus._MESSAGEROOM_FORBIDDEN);
    }

    private void tryHardDeleteMessageRoom(MessageRoom messageRoom) {
        LocalDateTime memberALastDeleteAt = messageRoom.getMemberALastDeleteAt();
        LocalDateTime memberBLastDeleteAt = messageRoom.getMemberBLastDeleteAt();

        // memberA or memberB 하나라도 null인 경우
        if (messageRoomValidator.isAnyMemberNullInMessageRoom(messageRoom)) {
            hardDeleteMessageRoom(messageRoom);
            return;
        }

        // 멤버 둘다 LastDeleteAt이 있고, 해당 쪽지방의 마지막 쪽지 createAt보다 최근인 경우
        if (messageRoomValidator.isBothMembersDeleteAtNotNull(memberALastDeleteAt, memberBLastDeleteAt)
            && messageRoomValidator.isDeletableHard(memberALastDeleteAt, memberBLastDeleteAt,
            messageRoom)) {
            hardDeleteMessageRoom(messageRoom);
        }
    }

    private void hardDeleteMessageRoom(MessageRoom messageRoom) {
        messageRepositoryService.deleteMessageByMessageRoom(messageRoom);
        messageRoomRepositoryService.deleteMessageRoom(messageRoom);
    }
}
package com.cozymate.cozymate_server.domain.message.service;

import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.dto.request.CreateMessageRequestDTO;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
import com.cozymate.cozymate_server.domain.fcm.event.converter.EventConverter;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.message.converter.MessageConverter;
import com.cozymate.cozymate_server.domain.messageroom.converter.MessageRoomConverter;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageCommandService {

    private final MessageRepositoryService messageRepositoryService;
    private final MessageRoomRepositoryService messageRoomRepositoryService;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MessageRoomIdResponseDTO createMessage(CreateMessageRequestDTO createMessageRequestDTO,
        Member sender, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MESSAGE_NOT_FOUND_RECIPIENT));

        Optional<MessageRoom> findMessageRoom = messageRoomRepositoryService.getMessageRoomByMemberAAndMemberBOptional(
            sender, recipient);

        MessageRoom messageRoom;
        if (findMessageRoom.isPresent()) {
            messageRoom = findMessageRoom.get();
        } else {
            messageRoom = MessageRoomConverter.toEntity(sender, recipient);
            messageRoom = messageRoomRepositoryService.createMessageRoom(messageRoom);
        }

        saveMessage(messageRoom, sender, createMessageRequestDTO.content());

        eventPublisher.publishEvent(
            EventConverter.toSentMessageEvent(sender, recipient, createMessageRequestDTO.content(),
                messageRoom));
        return MessageRoomConverter.toMessageRoomIdResponseDTO(messageRoom.getId());
    }

    private void saveMessage(MessageRoom messageRoom, Member sender, String content) {
        Message message = MessageConverter.toEntity(messageRoom, sender, content.trim());
        messageRepositoryService.createMessage(message);
    }
}
package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.fcm.event.converter.EventConverter;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
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
public class ChatCommandService {

    private final ChatRepositoryService chatRepositoryService;
    private final ChatRoomRepositoryService chatRoomRepositoryService;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ChatRoomIdResponseDTO createChat(CreateChatRequestDTO createChatRequestDTO,
        Member sender, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHAT_NOT_FOUND_RECIPIENT));

        Optional<ChatRoom> findChatRoom = chatRoomRepositoryService.getChatRoomByMemberAAndMemberBOptional(
            sender, recipient);

        ChatRoom chatRoom;
        if (findChatRoom.isPresent()) {
            chatRoom = findChatRoom.get();
        } else {
            chatRoom = ChatRoomConverter.toEntity(sender, recipient);
            chatRoom = chatRoomRepositoryService.createChatRoom(chatRoom);
        }

        saveChat(chatRoom, sender, createChatRequestDTO.content());

        eventPublisher.publishEvent(
            EventConverter.toSentChatEvent(sender, recipient, createChatRequestDTO.content(),
                chatRoom));
        return ChatRoomConverter.toChatRoomIdResponseDTO(chatRoom.getId());
    }

    private void saveChat(ChatRoom chatRoom, Member sender, String content) {
        Chat chat = ChatConverter.toEntity(chatRoom, sender, content.trim());
        chatRepositoryService.createChat(chat);
    }
}
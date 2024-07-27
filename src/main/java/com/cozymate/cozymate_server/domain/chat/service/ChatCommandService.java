package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.ChatRequestDto;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatCommandService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void createChat(ChatRequestDto chatRequestDto, Long recipientId) {

        Member sender = memberRepository.findById(chatRequestDto.getSenderId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByMemberAAndMemberB(sender,
            recipient);

        if (findChatRoom.isPresent()) {
            saveChat(findChatRoom.get(), sender, chatRequestDto.getContent());
        } else {
            ChatRoom chatRoom = ChatRoomConverter.toEntity(sender, recipient);
            chatRoomRepository.save(chatRoom);

            saveChat(chatRoom, sender, chatRequestDto.getContent());
        }
    }

    private void saveChat(ChatRoom chatRoom, Member sender, String content) {
        Chat chat = ChatConverter.toEntity(chatRoom, sender, content);
        chatRepository.save(chat);
    }
}
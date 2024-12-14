package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.memberblock.util.MemberBlockUtil;
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
    private final MemberBlockUtil memberBlockUtil;

    public ChatRoomIdResponseDTO createChat(CreateChatRequestDTO createChatRequestDTO, Member sender, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHAT_NOT_FOUND_RECIPIENT));

        checkBlockedMember(sender, recipientId);

        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByMemberAAndMemberB(sender,
            recipient);

        if (findChatRoom.isPresent()) {
            saveChat(findChatRoom.get(), sender, createChatRequestDTO.content().trim());

            return ChatRoomConverter.toChatRoomIdResponseDTO(findChatRoom.get().getId());
        } else {
            ChatRoom chatRoom = ChatRoomConverter.toEntity(sender, recipient);
            chatRoomRepository.save(chatRoom);
            saveChat(chatRoom, sender, createChatRequestDTO.content());

            return ChatRoomConverter.toChatRoomIdResponseDTO(chatRoom.getId());
        }
    }

    private void checkBlockedMember(Member sender, Long recipientId) {
        if (memberBlockUtil.existsMemberBlock(sender, recipientId)) {
            throw new GeneralException(ErrorStatus._REQUEST_TO_BLOCKED_MEMBER);
        }
    }

    private void saveChat(ChatRoom chatRoom, Member sender, String content) {
        Chat chat = ChatConverter.toEntity(chatRoom, sender, content);
        chatRepository.save(chat);
    }
}
package com.cozymate.cozymate_server.domain.chat.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatStreamDTO;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatPubDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberCachingDTO;
import java.time.LocalDateTime;
import java.util.List;

public class ChatConverter {

    public static Chat toDocument(ChatStreamDTO chatStreamDTO, LocalDateTime createdAt,
        Long sequence) {
        return Chat.builder()
            .chatRoomId(Long.valueOf(chatStreamDTO.chatRoomId()))
            .memberId(chatStreamDTO.memberId())
            .content(chatStreamDTO.content())
            .createdAt(createdAt)
            .sequence(sequence)
            .build();
    }

    public static ChatPubDTO toChatPubDTO(CreateChatRequestDTO createChatRequestDTO,
        MemberCachingDTO memberCachingDTO) {
        return ChatPubDTO.builder()
            .chatRoomId(createChatRequestDTO.chatRoomId())
            .persona(memberCachingDTO.persona())
            .memberId(createChatRequestDTO.memberId())
            .nickname(memberCachingDTO.nickname())
            .content(createChatRequestDTO.content())
            .createdAt(LocalDateTime.now())
            .sequence(0l)
            .build();
    }

    public static ChatResponseDTO toChatResponseDTO(Chat chat, String nickname, Integer persona) {
        return ChatResponseDTO.builder()
            .chatRoomId(chat.getChatRoomId())
            .persona(persona)
            .memberId(chat.getMemberId())
            .nickname(nickname)
            .content(chat.getContent())
            .createdAt(chat.getCreatedAt())
            .sequence(chat.getSequence())
            .build();
    }

    public static ChatListResponseDTO toChatListResponseDTO(boolean hasNext,
        List<ChatResponseDTO> chatResponseDTOList) {
        return ChatListResponseDTO.builder()
            .hasNext(hasNext)
            .chatResponseDTOList(chatResponseDTOList)
            .build();
    }
}

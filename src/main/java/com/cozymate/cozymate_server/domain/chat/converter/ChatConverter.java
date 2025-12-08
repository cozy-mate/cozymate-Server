package com.cozymate.cozymate_server.domain.chat.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatPubDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberCachingDTO;
import java.time.LocalDateTime;
import java.util.List;

public class ChatConverter {

    public static Chat toDocument(CreateChatRequestDTO createChatRequestDTO) {
        return Chat.builder()
            .chatRoomId(Long.valueOf(createChatRequestDTO.chatRoomId()))
            .memberId(createChatRequestDTO.memberId())
            .content(createChatRequestDTO.content())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static ChatPubDTO toChatPubDTO(Chat chat, MemberCachingDTO memberCachingDTO) {
        return ChatPubDTO.builder()
            .chatRoomId(chat.getChatRoomId())
            .persona(memberCachingDTO.persona())
            .memberId(chat.getMemberId())
            .nickname(memberCachingDTO.nickname())
            .content(chat.getContent())
            .createdAt(chat.getCreatedAt())
            .build();
    }

    public static ChatResponseDTO toChatResponseDTO(Chat chat, String nickname, Integer persona) {
        return ChatResponseDTO.builder()
            .chatId(chat.getId())
            .chatRoomId(chat.getChatRoomId())
            .persona(persona)
            .memberId(chat.getMemberId())
            .nickname(nickname)
            .content(chat.getContent())
            .createdAt(chat.getCreatedAt())
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

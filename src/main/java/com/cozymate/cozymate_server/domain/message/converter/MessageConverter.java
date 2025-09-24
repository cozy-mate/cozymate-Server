package com.cozymate.cozymate_server.domain.message.converter;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.dto.response.MessageContentResponseDTO;
import com.cozymate.cozymate_server.domain.message.dto.response.MessageListResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageConverter {

    public static Message toEntity(MessageRoom messageRoom, Member sender, String content) {
        return Message.builder()
            .messageRoom(messageRoom)
            .sender(sender)
            .content(content)
            .build();
    }

    public static MessageContentResponseDTO toMessageContentResponseDTO(String nickname, String content,
        LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd | HH:mm");
        String formattedDateTime = createdAt.format(formatter);

        return MessageContentResponseDTO.builder()
            .nickname(nickname)
            .content(content)
            .datetime(formattedDateTime)
            .build();
    }

    public static MessageListResponseDTO toMessageResponseDTO(Long recipientId,
        List<MessageContentResponseDTO> messageContentResponseDTOList) {
        return MessageListResponseDTO.builder()
            .memberId(recipientId)
            .content(messageContentResponseDTOList)
            .build();
    }
}
package com.cozymate.cozymate_server.domain.messageroom.converter;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.messageroom.dto.MessageRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.CountMessageRoomsWithNewMessageDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;

public class MessageRoomConverter {

    public static MessageRoom toEntity(Member sender, Member recipient) {
        return MessageRoom.builder()
            .memberA(sender)
            .memberB(recipient)
            .build();
    }

    public static MessageRoomDetailResponseDTO toMessageRoomDetailResponseDTO(String nickname,
        String content, Long messageRoomId) {
        return MessageRoomDetailResponseDTO.builder()
            .nickname(nickname)
            .lastContent(content.trim())
            .messageRoomId(messageRoomId)
            .persona(null)
            .memberId(null)
            .hasNewMessage(false)
            .build();
    }

    public static MessageRoomDetailResponseDTO toMessageRoomDetailResponseDTO(Member member, Message message,
        MessageRoom messageRoom, boolean hasNewMessage) {

        Member recipient = member.getId().equals(messageRoom.getMemberA().getId()) ?
            messageRoom.getMemberB() : messageRoom.getMemberA();

        return MessageRoomDetailResponseDTO.builder()
            .nickname(recipient.getNickname())
            .lastContent(message.getContent().trim())
            .messageRoomId(messageRoom.getId())
            .persona(recipient.getPersona())
            .memberId(recipient.getId())
            .hasNewMessage(hasNewMessage)
            .build();
    }

    public static MessageRoomIdResponseDTO toMessageRoomIdResponseDTO(Long messageRoomId) {
        return MessageRoomIdResponseDTO.builder()
            .messageRoomId(messageRoomId)
            .build();
    }

    public static MessageRoomSimpleDTO toMessageRoomSimpleDTO(Optional<MessageRoom> messageRoom,
        Member recipient) {
        return MessageRoomSimpleDTO.builder()
            .messageRoom(messageRoom)
            .recipient(recipient)
            .build();
    }

    public static CountMessageRoomsWithNewMessageDTO toCountMessageRoomsWithNewMessageDTO(
        Integer messageRoomsWithNewMessageCount) {
        return CountMessageRoomsWithNewMessageDTO.builder()
            .messageRoomsWithNewMessageCount(messageRoomsWithNewMessageCount)
            .build();
    }
}
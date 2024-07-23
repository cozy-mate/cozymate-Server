package com.cozymate.cozymate_server.domain.chat;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void createChat(ChatRequestDto chatRequestDto, Long recipientId) {

        Member sender = memberRepository.findById(chatRequestDto.getSenderId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._NOT_FOUND_MEMBER)
            );
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._NOT_FOUND_MEMBER)
            );

        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByMemberAAndMemberB(sender,
            recipient);

        if (findChatRoom.isPresent()) {
            saveChat(chatRequestDto.getContent(), findChatRoom.get(), sender);
        } else {
            ChatRoom chatRoom = ChatRoom.builder()
                .memberA(sender)
                .memberB(recipient)
                .build();
            chatRoomRepository.save(chatRoom);

            saveChat(chatRequestDto.getContent(), chatRoom, sender);
        }
    }

    private void saveChat(String content, ChatRoom chatRoom, Member sender) {
        Chat chat = Chat.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .build();
        chatRepository.save(chat);
    }
}
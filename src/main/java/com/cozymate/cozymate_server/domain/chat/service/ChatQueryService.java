package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public List<ChatResponseDto> getChatList(Long memberId, Long chatRoomId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        //chatRoomId으로 채팅방 찾아오기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));

        //chatRoom의 chat 전부 찾아오기
        List<Chat> findChatList = chatRepository.findAllByChatRoom(chatRoom);

        //chatRoom에서 내가 delete한 시간 이후에 오고 간 chat만 분류하기
        LocalDateTime memberLastDeleteAt =
            chatRoom.getMemberA().getNickname().equals(member.getNickname())
                ? chatRoom.getMemberALastDeleteAt()
                : chatRoom.getMemberBLastDeleteAt();

        List<Chat> chatList = findChatList.stream()
            .filter(chat -> memberLastDeleteAt == null || chat.getCreatedAt()
                .isAfter(memberLastDeleteAt))
            .collect(Collectors.toList());

        List<ChatResponseDto> chatResponseDtoList = chatList.stream()
            .map(chat -> {
                String senderNickName = chat.getSender().getNickname();
                String nickName =
                    senderNickName.equals(member.getNickname()) ? senderNickName + " (나)"
                        : senderNickName;
                return ChatConverter.toResponseDto(nickName, chat.getContent(),
                    chat.getCreatedAt());
            })
            .collect(Collectors.toList());

        return chatResponseDtoList;
    }
}
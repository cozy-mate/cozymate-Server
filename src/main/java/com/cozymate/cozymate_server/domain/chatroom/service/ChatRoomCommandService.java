package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    public void deleteChatRoom(Long myId, Long chatRoomId) {
        //chatRoomId로 chatRoom 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));

        // chatRoom의 memberA와 memberB 중 나와 일치 하는 A or B를 찾고 마지막 삭제 시간을 업데이트 (논리적 삭제)
        softDeleteChatRoom(chatRoom, myId);

        // chatRoom에 두 멤버의 삭제 시간이 해당 chatRoom의 마지막 chat의 생성시간 보다 이후 일 경우 db 물리적 삭제
        hardDeleteChatRoom(chatRoom);
    }

    private void softDeleteChatRoom(ChatRoom chatRoom, Long myId) {
        if (chatRoom.getMemberA().getId().equals(myId)) {
            chatRoom.updateMemberALastDeleteAt();
        } else if (chatRoom.getMemberB().getId().equals(myId)) {
            chatRoom.updateMemberBLastDeleteAt();
        } else {
            throw new GeneralException(ErrorStatus._CHATROOM_FORBIDDEN);
        }
    }

    private void hardDeleteChatRoom(ChatRoom chatRoom) {
        chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .ifPresent(chat -> {
                boolean deleteByMemberA =
                    chatRoom.getMemberALastDeleteAt() != null && chat.getCreatedAt()
                        .isBefore(chatRoom.getMemberALastDeleteAt());
                boolean deleteByMemberB =
                    chatRoom.getMemberBLastDeleteAt() != null && chat.getCreatedAt()
                        .isBefore(chatRoom.getMemberBLastDeleteAt());

                if (deleteByMemberA && deleteByMemberB) {
                    chatRepository.deleteAllByChatRoom(chatRoom);
                    chatRoomRepository.delete(chatRoom);
                }
            });
    }
}
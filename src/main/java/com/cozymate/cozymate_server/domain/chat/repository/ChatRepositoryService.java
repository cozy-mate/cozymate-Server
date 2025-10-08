package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRepositoryService {

    private final ChatRepository chatRepository;

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    public List<Chat> getChatListByRange(Long chatRoomId, LocalDateTime enterTime,
        LocalDateTime lastChatTime, Long sequence, PageRequest pageRequest) {
        return chatRepository.findChatsInRange(chatRoomId, enterTime, lastChatTime, sequence,
            pageRequest);
    }
}

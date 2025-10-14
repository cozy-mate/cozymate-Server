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

    public List<Chat> getChatListInRange(Long chatRoomId, LocalDateTime enterTime,
        LocalDateTime lastChatTime, String chatId, PageRequest pageRequest) {
        return chatRepository.findChatsInRange(chatRoomId, enterTime, lastChatTime, chatId,
            pageRequest);
    }

    public List<Chat> getChatListAfterEnterTime(Long chatRoomId, LocalDateTime enterTime,
        PageRequest pageRequest) {
        return chatRepository.findChatsAfterEnterTime(chatRoomId, enterTime, pageRequest);
    }
}

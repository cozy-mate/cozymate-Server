package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatRepository extends MongoRepository<Chat, String> {

    @Query("""
    {
        'chatRoomId': ?0,
        $or: [
            {'createdAt': { $gt: ?1, $lt: ?2 } },
            { 'createdAt': ?2, '_id': { $lt: ?3 } }
        ]
    }
    """)
    List<Chat> findChatsInRange(
        Long chatRoomId,             // ?0
        LocalDateTime enterTime,     // ?1
        LocalDateTime lastChatTime,  // ?2
        String chatId,               // ?3
        Pageable pageable
    );

    @Query("""
    {
        'chatRoomId': ?0,
        'createdAt': {$gt: ?1}
    }
    """)
    List<Chat> findChatsAfterEnterTime(
        Long chatRoomId,             // ?0
        LocalDateTime enterTime,     // ?1
        Pageable pageable
    );
}

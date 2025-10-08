package com.cozymate.cozymate_server.domain.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@Getter
@Document("chat")
public class Chat {

    @Id
    private String id;          // MongoDB ObjectId
    private Long chatRoomId;      // 채팅방 ID
    private Long memberId;    // 메시지 보낸 사람
    private String content;     // 메시지 내용
    private LocalDateTime createdAt;  // 생성 시간
    private Long sequence;   // redis stream에서 할당된 값
}

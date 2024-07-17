package com.cozymate.cozymate_server.domain.report;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mate reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom = null;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post = null;

    @ManyToOne(fetch = FetchType.LAZY)
    private PostComment comment = null;

    private String reason;
}

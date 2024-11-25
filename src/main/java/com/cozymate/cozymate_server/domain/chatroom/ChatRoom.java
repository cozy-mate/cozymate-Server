package com.cozymate.cozymate_server.domain.chatroom;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member memberA;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member memberB;

    private LocalDateTime memberALastDeleteAt;

    private LocalDateTime memberBLastDeleteAt;

    public void updateMemberALastDeleteAt() {
        this.memberALastDeleteAt = LocalDateTime.now();
    }

    public void updateMemberBLastDeleteAt() {
        this.memberBLastDeleteAt = LocalDateTime.now();
    }
    public boolean isEmpty() {
        return (this.memberA == null && this.memberB == null);
    }
}
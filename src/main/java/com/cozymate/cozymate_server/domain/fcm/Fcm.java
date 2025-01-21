package com.cozymate.cozymate_server.domain.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Fcm extends BaseTimeEntity {

    @Id
    @Column(name = "device_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String token;

    private boolean isValid;

    public void updateToken(String token) {
        this.token = token;
        this.isValid = true;
    }
}
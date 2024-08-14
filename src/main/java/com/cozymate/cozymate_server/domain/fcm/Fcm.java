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
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    Member member;

    String token;

    public void updateToken(String token) {
        this.token = token;
    }
}
package com.cozymate.cozymate_server.domain.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import jakarta.persistence.Entity;
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
public class Fcm {

    @Id
    String deviceId;
    String token;
    @ManyToOne
    Member member;
}
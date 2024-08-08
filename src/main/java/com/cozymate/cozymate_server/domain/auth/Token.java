package com.cozymate.cozymate_server.domain.auth;


import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Token extends BaseTimeEntity {
    @Id
    @NonNull
    private String userName;


    @Getter
    @NonNull
    private String refreshToken;
}

package com.cozymate.cozymate_server.domain.mail;

import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class MailAuthentication extends BaseTimeEntity {
    @Id
    private Long memberId;

    private String mailAddress;
    private String code;
    private Boolean isVerified;
}

package com.cozymate.cozymate_server.domain.member.dto.request;

import org.hibernate.validator.constraints.Length;

public record WithdrawRequestDTO(
    @Length(max = 100, message = "탈퇴 사유는 최대 100자")
    String withdrawReason
) {
}

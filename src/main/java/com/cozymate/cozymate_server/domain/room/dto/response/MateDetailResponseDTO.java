package com.cozymate.cozymate_server.domain.room.dto.response;

public record MateDetailResponseDTO(
    Long memberId,
    Long mateId,
    String nickname,
    Integer persona,
    Integer mateEquality
){

}

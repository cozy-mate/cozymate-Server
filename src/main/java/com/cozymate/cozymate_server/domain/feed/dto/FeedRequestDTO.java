package com.cozymate.cozymate_server.domain.feed.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeedRequestDTO(

    @NotNull
    Long roomId,

    //TODO: 피드가 학교 단위로 확장되면 사용
    //Long universityId,

    @NotBlank
    String name,

    @NotBlank
    String description
) {

}

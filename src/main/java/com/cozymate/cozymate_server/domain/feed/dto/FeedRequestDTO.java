package com.cozymate.cozymate_server.domain.feed.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequestDTO {

    @NotNull
    private Long roomId;

    //TODO: 피드가 학교 단위로 확장되면 사용
    //private Long universityId;
    @NotBlank
    private String name;

    @NotBlank
    private String description;

}

package com.cozymate.cozymate_server.domain.room.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

@Getter
public class PublicRoomCreateRequest {

    @NotBlank(message = "방 이름은 필수입니다.")
    @Size(max=12, message = "방 이름은 최대 12글자입니다.")
    @Pattern(regexp = "^(?!\\s)[가-힣a-zA-Z0-9\\s]+(?<!\\s)$", message = "한글, 영어, 숫자 및 공백만 입력해주세요. 단, 공백은 처음이나 끝에 올 수 없습니다.")
    private String name;
    @NotNull(message = "프로필 이미지 선택은 필수입니다.")
    @Min(0)
    @Max(15)
    private Integer profileImage;
    @NotNull(message = "방 인원수 선택은 필수입니다.")
    @Min(value = 2, message = "방 인원은 2~6명 사이여야 합니다.")
    @Max(value = 6, message = "방 인원은 2~6명 사이여야 합니다.")
    private Integer maxMateNum;
    @Size(min = 1, max = 3, message = "해시태그는 1개에서 3개까지 입력할 수 있습니다.")
    private List<@NotBlank @Pattern(regexp = "^(?!_)[가-힣a-zA-Z0-9]+(_[가-힣a-zA-Z0-9]+)*(?<!_)$",
        message = "해시태그는 한글, 영어, 숫자 및 '_'만 사용할 수 있으며, '_'는 앞이나 뒤에 올 수 없습니다.") String> hashtags;

}

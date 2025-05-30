package com.cozymate.cozymate_server.domain.room.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.Range;

public record RoomUpdateRequestDTO(
    @NotBlank(message = "방 이름은 필수입니다.")
    @Size(max=12, message = "방 이름은 최대 12글자입니다.")
    @Pattern(regexp = "^(?!\\s)[가-힣a-zA-Z0-9\\s]+(?<!\\s)$", message = "한글, 영어, 숫자 및 공백만 입력해주세요. 단, 공백은 처음이나 끝에 올 수 없습니다.")
    String name,
    @NotNull(message = "프로필 이미지 선택은 필수입니다.")
    @Range(min=1, max=16)
    Integer persona,
    @Size(min = 1, max = 3, message = "해시태그는 1개에서 3개까지 입력할 수 있습니다.")
    List<@Size(min = 1, max = 5, message = "해시태그는 1글자에서 5글자까지 입력할 수 있습니다.")
        @NotBlank @Pattern(regexp = "^(?!_)[가-힣a-zA-Z0-9]+(_[가-힣a-zA-Z0-9]+)*(?<!_)$",
        message = "해시태그는 한글, 영어, 숫자 및 '_'만 사용할 수 있으며, '_'는 앞이나 뒤에 올 수 없습니다.") String> hashtagList
) {

}

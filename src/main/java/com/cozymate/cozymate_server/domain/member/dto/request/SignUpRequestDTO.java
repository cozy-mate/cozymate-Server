package com.cozymate.cozymate_server.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;


/**
 * {
 *     "nickname": "말즈",
 *     "gender": "MALE",
 *     "birthday": "2000-01-20",
 *     "persona" : 1,
 *     "university : 1
 * }
 */

@Builder
public record SignUpRequestDTO(
    @NotNull
    @NotEmpty
    String nickname,

    @NotNull
    String gender,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate birthday,

    @NotNull
    @Max(value = 16)
    @Min(value = 1)
    Integer persona,

    @NotNull
    Long universityId
) {
}


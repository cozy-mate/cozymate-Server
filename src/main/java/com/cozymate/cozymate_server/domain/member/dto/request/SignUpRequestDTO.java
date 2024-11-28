package com.cozymate.cozymate_server.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;


/**
 * { "nickname": "말즈", "gender": "MALE", "birthday": "2000-01-20", "persona" : 1, "university : 1 }
 */

@Builder
public record SignUpRequestDTO(
    @NotEmpty(message = "{RequestFiledNotEmpty}")
    String nickname,

    @NotNull(message = "{RequestFiledNotNull}")
    String gender,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "{RequestFiledNotNull}")
    LocalDate birthday,

    @NotNull(message = "{RequestFiledNotNull}")
    @Range(min = 1, max = 16, message = "{RequestFieldRange}")
    Integer persona,

    @NotNull(message = "{RequestFiledNotNull}")
    Long universityId
) {

}


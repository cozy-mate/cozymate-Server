package com.cozymate.cozymate_server.domain.memberstatpreference.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatPreferenceDto {

    @Size(min = 4, max = 4)
    public List<String> preferences;
}

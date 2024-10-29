package com.cozymate.cozymate_server.domain.university.dto;

import com.cozymate.cozymate_server.domain.university.dto.UniversityRequest.UniversityDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UniversityResponse {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UniversityDTO{
        private String name;
        private String mailPattern;
        private List<String> dormitoryNames;
        private List<String> departments;
    }
}

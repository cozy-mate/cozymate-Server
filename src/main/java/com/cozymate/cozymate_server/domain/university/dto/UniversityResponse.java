package com.cozymate.cozymate_server.domain.university.dto;

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
        private Long id;
        private String name;
        private String mailPattern;
        private List<String> dormitoryNames;
        private List<String> departments;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UniversityBasicInfoDTO{
        private String name;
        private Long id;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UniversityListDTO{
        private List<UniversityBasicInfoDTO> universityList;
    }
}

package com.cozymate.cozymate_server.domain.university.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UniversityRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniversityDTO{
        String name;
        String mailPattern;
        List<String> dormitoryNames;
        List<String> departments;
    }

}

package com.cozymate.cozymate_server.domain.university.converter;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.dto.UniversityRequest;
import com.cozymate.cozymate_server.domain.university.dto.UniversityResponse;

public class UniversityConverter {
    public static University toUniversity(UniversityRequest.UniversityDTO universityDTO){
        return University.builder()
                .name(universityDTO.getName())
                .dormitoryNames(universityDTO.getDormitoryNames())
                .departments(universityDTO.getDepartments())
                .build();
    }

    public static UniversityResponse.UniversityDTO toUniversityDTO(University university){
        return UniversityResponse.UniversityDTO.builder()
                .name(university.getName())
                .mailPattern(university.getMailPattern())
                .dormitoryNames(university.getDormitoryNames())
                .departments(university.getDepartments())
                .build();
    }



}

package com.cozymate.cozymate_server.domain.university.converter;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.dto.UniversityRequest;
import com.cozymate.cozymate_server.domain.university.dto.UniversityResponse;
import java.util.List;
import java.util.stream.Collectors;

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
                .id(university.getId())
                .name(university.getName())
                .mailPattern(university.getMailPattern())
                .dormitoryNames(university.getDormitoryNames())
                .departments(university.getDepartments())
                .build();
    }

    public static UniversityResponse.UniversityBasicInfoDTO toUniversityBasicInfoDTO(University university){
        return UniversityResponse.UniversityBasicInfoDTO.builder()
                .id(university.getId())
                .name(university.getName())
                .build();
    }
    public static UniversityResponse.UniversityListDTO toUniversityListDTO(List<University> universityList){
        List<UniversityResponse.UniversityBasicInfoDTO> universityDTOList = universityList.stream()
                .map(UniversityConverter::toUniversityBasicInfoDTO)
                .collect(Collectors.toList());

        return UniversityResponse.UniversityListDTO.builder()
                .universityList(universityDTOList)
                .build();

    }


}

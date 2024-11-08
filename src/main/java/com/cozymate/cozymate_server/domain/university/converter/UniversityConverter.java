package com.cozymate.cozymate_server.domain.university.converter;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.dto.request.UniversityRequestDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityBasicInfoDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityDetailDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityListDTO;
import java.util.List;
import java.util.stream.Collectors;

public class UniversityConverter {
    public static University toUniversity(UniversityRequestDTO universityDTO){
        return University.builder()
                .name(universityDTO.name())
                .dormitoryNames(universityDTO.dormitoryNames())
                .departments(universityDTO.departments())
                .build();
    }

    public static UniversityDetailDTO toUniversityDTOFromEntity(University university){
        return UniversityDetailDTO.builder()
                .id(university.getId())
                .name(university.getName())
                .mailPattern(university.getMailPattern())
                .dormitoryNames(university.getDormitoryNames())
                .departments(university.getDepartments())
                .build();
    }

    public static UniversityBasicInfoDTO toUniversityBasicInfoDTOFromEntity(University university){
        return UniversityBasicInfoDTO.builder()
                .id(university.getId())
                .name(university.getName())
                .build();
    }
    public static UniversityListDTO toUniversityListDTO(List<University> universityList){
        List<UniversityBasicInfoDTO> universityDTOList = universityList.stream()
                .map(UniversityConverter::toUniversityBasicInfoDTOFromEntity)
                .collect(Collectors.toList());

        return UniversityListDTO.builder()
                .universityList(universityDTOList)
                .build();

    }


}

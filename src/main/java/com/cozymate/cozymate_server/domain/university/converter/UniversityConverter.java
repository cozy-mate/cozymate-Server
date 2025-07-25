package com.cozymate.cozymate_server.domain.university.converter;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.dto.request.UniversityRequestDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityBasicInfoResponseDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityListResponseDTO;
import java.util.List;
import java.util.stream.Collectors;

public class UniversityConverter {

  public static University toUniversity(UniversityRequestDTO universityDTO) {
    return University.builder()
        .name(universityDTO.name())
        .mailPatterns(universityDTO.mailPatterns())
        .dormitoryNames(universityDTO.dormitoryNames())
        .departments(universityDTO.departments())
        .build();
  }

  public static UniversityDetailResponseDTO toUniversityDTOFromEntity(University university) {
    return UniversityDetailResponseDTO.builder()
        .id(university.getId())
        .name(university.getName())
        .mailPatterns(university.getMailPatterns())
        .mailPattern(university.getMailPatterns().get(0))
        .dormitoryNames(university.getDormitoryNames().stream().sorted().toList())
        .departments(university.getDepartments().stream().sorted().toList())
        .build();
  }

  public static UniversityBasicInfoResponseDTO toUniversityBasicInfoDTOFromEntity(
      University university) {
    return UniversityBasicInfoResponseDTO.builder()
        .id(university.getId())
        .name(university.getName())
        .build();
  }

  public static UniversityListResponseDTO toUniversityListDTO(List<University> universityList) {
    List<UniversityBasicInfoResponseDTO> universityDTOList = universityList.stream()
        .map(UniversityConverter::toUniversityBasicInfoDTOFromEntity)
        .collect(Collectors.toList());

    return UniversityListResponseDTO.builder()
        .universityList(universityDTOList)
        .build();

  }


}

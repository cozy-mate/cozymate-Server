package com.cozymate.cozymate_server.domain.university.service;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.converter.UniversityConverter;
import com.cozymate.cozymate_server.domain.university.dto.request.UniversityRequestDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.university.dto.response.UniversityListResponseDTO;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniversityService {
    private final UniversityRepository universityRepository;


    @Transactional
    public UniversityDetailResponseDTO createUniversity(UniversityRequestDTO requestDTO) {
        University university = UniversityConverter.toUniversity(requestDTO);
        universityRepository.save(university);

        return UniversityConverter.toUniversityDTOFromEntity(university);
    }

    @Transactional
    public UniversityDetailResponseDTO updateUniversity(UniversityRequestDTO requestDTO) {
        University university = universityRepository.findByName(requestDTO.name())
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        university.update(requestDTO.mailPattern(), requestDTO.departments(), requestDTO.dormitoryNames());

        return UniversityConverter.toUniversityDTOFromEntity(university);
    }

    public UniversityDetailResponseDTO getUniversity(Long id) {
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));
        return UniversityConverter.toUniversityDTOFromEntity(university);
    }

    public UniversityListResponseDTO getUniversities(){
        List<University> universityList = universityRepository.findAll();

        return UniversityConverter.toUniversityListDTO(universityList);
    }

    public UniversityDetailResponseDTO getMemberUniversity(MemberDetails memberDetails){
        return  UniversityConverter.toUniversityDTOFromEntity(memberDetails.member().getUniversity());
    }

}

package com.cozymate.cozymate_server.domain.university.service;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.converter.UniversityConverter;
import com.cozymate.cozymate_server.domain.university.dto.UniversityRequest;
import com.cozymate.cozymate_server.domain.university.dto.UniversityResponse;
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
    public UniversityResponse.UniversityDTO createUniversity(UniversityRequest.UniversityDTO requestDTO) {
        University university = UniversityConverter.toUniversity(requestDTO);
        universityRepository.save(university);

        return UniversityConverter.toUniversityDTO(university);
    }

    @Transactional
    public UniversityResponse.UniversityDTO updateUniversity(UniversityRequest.UniversityDTO requestDTO) {
        University university = universityRepository.findByName(requestDTO.getName())
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        university.update(requestDTO.getMailPattern(), requestDTO.getDepartments(), requestDTO.getDormitoryNames());

        return UniversityConverter.toUniversityDTO(university);
    }

    public UniversityResponse.UniversityDTO getUniversity(Long id) {
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));
        return UniversityConverter.toUniversityDTO(university);
    }

    public UniversityResponse.UniversityListDTO getUniversities(){
        List<University> universityList = universityRepository.findAll();

        return UniversityConverter.toUniversityListDTO(universityList);
    }

    public UniversityResponse.UniversityDTO getMemberUniversity(MemberDetails memberDetails){
        return  UniversityConverter.toUniversityDTO(memberDetails.getMember().getUniversity());
    }

}

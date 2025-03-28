package com.cozymate.cozymate_server.domain.university.repository;


import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniversityRepositoryService {

    private final UniversityRepository universityRepository;

    public University getUniversityByIdOrThrow(Long id) {
        return universityRepository.findById(id)
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));
    }

    public University getUniversityByNameOrThrow(String name) {
        return universityRepository.findByName(name)
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));
    }

    public University createUniversity(University university){
        return universityRepository.save(university);
    }

    public List<University> getAllUniversityList(){
        return universityRepository.findAll();
    }

}

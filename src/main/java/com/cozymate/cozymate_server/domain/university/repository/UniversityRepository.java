package com.cozymate.cozymate_server.domain.university.repository;

import com.cozymate.cozymate_server.domain.university.University;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional <University> findByName(String name);
}

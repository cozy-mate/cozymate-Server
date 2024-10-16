package com.cozymate.cozymate_server.global.redis.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

        private final TestRepository testRepository;

        public void saveTestEntity(Long id, String name) {
            Test testEntity = Test.builder()
                .id(id)
                .name(name)
                .build();

            testRepository.save(testEntity);
        }

        public Test findTestEntity(Long id) {
            return testRepository.findById(id).orElse(null);
        }

}

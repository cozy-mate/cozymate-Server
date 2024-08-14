package com.cozymate.cozymate_server.domain.fcm.service;

import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.FcmTestBuilder;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmRequestDto;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("FcmCommandService 클래스의")
class FcmCommandServiceTest {

    @Mock
    FcmRepository fcmRepository;
    @InjectMocks
    FcmCommandService fcmCommandService;
    Member member;
    Fcm fcm;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class createFcm_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 기존_기기의_fcm_저장인_경우 {

            @BeforeEach
            void setUp() {
                member = FcmTestBuilder.testMemberBuild();
                fcm = FcmTestBuilder.testIPhoneFcmBuild();

                given(fcmRepository.findById(fcm.getId())).willReturn(Optional.of(fcm));
            }

            @Test
            @DisplayName("해당 fcm의 token 값을 변경한다.")
            void it_returns_update_token_value() {
                fcmCommandService.createFcm(member,
                    new FcmRequestDto(fcm.getId(), "new token value"));

                then(fcmRepository).should(never()).save(any(Fcm.class));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 새로운_기기의_fcm_저장인_경우 {

            @BeforeEach
            void setUp() {
                member = FcmTestBuilder.testMemberBuild();
                fcm = FcmTestBuilder.testIPhoneFcmBuild();

                given(fcmRepository.findById(fcm.getId())).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("새로운 fcm 엔티티를 저장한다.")
            void it_returns_new_entity_save() {
                fcmCommandService.createFcm(member, new FcmRequestDto(fcm.getId(), fcm.getToken()));

                then(fcmRepository).should(times(1)).save(any(Fcm.class));
            }
        }
    }
}
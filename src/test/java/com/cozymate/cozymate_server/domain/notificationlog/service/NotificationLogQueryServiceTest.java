package com.cozymate.cozymate_server.domain.notificationlog.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLogTestBuilder;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogResponseDto;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

@DisplayName("NotificationLogQueryService 클래스의")
@ExtendWith(MockitoExtension.class)
class NotificationLogQueryServiceTest {

    @Mock
    NotificationLogRepository notificationLogRepository;
    @InjectMocks
    NotificationLogQueryService notificationLogQueryService;
    Member memberA;
    NotificationLog notificationLogA;
    NotificationLog notificationLogC;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class sendNotification_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 카테고리를_선택할_경우 {

            @BeforeEach
            void setUp() {
                NotificationLog notificationLog1 = NotificationLogTestBuilder.testNotificationLogBuild();
                memberA = notificationLog1.getMember();

                notificationLogA = spy(notificationLog1);
                given(notificationLogA.getCreatedAt()).willReturn(
                    LocalDateTime.now().minusDays(1));
                given(notificationLogRepository.findByMemberAndCategoryOrderByIdDesc(memberA,
                    notificationLogA.getCategory())).willReturn(List.of(notificationLogA));

            }

            @DisplayName("해당 멤버의 카레고리에 맞는 알림 내역을 리스트로 반환한다.")
            @Test
            void it_returns_notification_log_list() {
                List<NotificationLogResponseDto> result = notificationLogQueryService.getNotificationLogList(
                    memberA, notificationLogA.getCategory());
                NotificationLogResponseDto resultResponse = result.get(0);

                assertThat(result.size()).isEqualTo(1);
                assertThat(resultResponse.getContent()).isEqualTo(
                    notificationLogA.getContent());
                assertThat(resultResponse.getCreatedAt()).isEqualTo("1일 전");
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 카테고리를_선택하지_않은_경우 {

            @BeforeEach
            void setUp() {
                NotificationLog notificationLog1 = NotificationLogTestBuilder.testNotificationLogBuild();
                NotificationLog notificationLog3 = NotificationLogTestBuilder.testNotificationLog2Build();
                memberA = notificationLog1.getMember();

                notificationLogA = spy(notificationLog1);
                given(notificationLogA.getCreatedAt()).willReturn(LocalDateTime.now().minusDays(1));
                notificationLogC = spy(notificationLog3);
                given(notificationLogC.getCreatedAt()).willReturn(
                    LocalDateTime.now().minusMinutes(5));
                given(notificationLogRepository.findByMemberOrderByIdDesc(memberA)).willReturn(
                    List.of(notificationLogA, notificationLogC));
            }

            @Test
            @DisplayName("멤버의 모든 알림 내역을 리스트로 반환한다.")
            void it_returns_all_notification_log_list() {
                List<NotificationLogResponseDto> result = notificationLogQueryService.getNotificationLogList(
                    memberA, null);

                assertThat(result.size()).isEqualTo(2);
                assertThat(result.get(0).getCreatedAt()).isEqualTo("1일 전");
                assertThat(result.get(0).getContent()).isEqualTo(notificationLogA.getContent());
                assertThat(result.get(1).getCreatedAt()).isEqualTo("5분 전");
                assertThat(result.get(1).getContent()).isEqualTo(notificationLogC.getContent());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 결과가_존재하지_않는_경우 {

            @BeforeEach
            void setUp() {
                memberA = NotificationLogTestBuilder.testNotificationLogBuild().getMember();

                given(notificationLogRepository.findByMemberAndCategoryOrderByIdDesc(any(Member.class), any(
                    NotificationCategory.class))).willReturn(new ArrayList<>());
            }

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void it_returns_empty_list() {
                List<NotificationLogResponseDto> result = notificationLogQueryService.getNotificationLogList(
                    memberA, NotificationCategory.COZY_HOME);

                assertThat(result).isEmpty();
            }
        }
    }
}
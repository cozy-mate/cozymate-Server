package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.fcm.NotificationContentDto;
import com.cozymate.cozymate_server.domain.fcm.NotificationService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import com.cozymate.cozymate_server.domain.fcm.NotificationTargetDto.GroupTargetDto;
import com.cozymate.cozymate_server.domain.fcm.NotificationTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.fcm.NotificationTargetDto.OneTargetReverseDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private NotificationLogRepository notificationLogRepository;
    @InjectMocks
    private NotificationService notificationService;

    private Member ve;
    private Member ro;
    private NotificationType notificationType;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Parameter_OneTargetDto_경우 {

        @BeforeEach
        void setUp() {
            ve = Member.builder()
                .fcmToken("dummy")
                .nickname("베")
                .socialType(SocialType.APPLE)
                .role(Role.USER)
                .clientId("aaa")
                .name("aaa")
                .birthDay(LocalDate.now())
                .persona(1)
                .gender(Gender.MALE)
                .build();
            notificationType = NotificationType.REMINDER_ROLE;
        }

        @Test
        void testSendNotification() throws FirebaseMessagingException {
            //given
            OneTargetDto oneTargetDto = OneTargetDto.create(ve, notificationType, "빨래하기");

            NotificationContentDto notificationContentDto = NotificationContentDto.create(
                oneTargetDto.getMember(), oneTargetDto.getRoleContent());
            String content = notificationType.generateContent(notificationContentDto);

            //when
            notificationService.sendNotification(oneTargetDto);

            // then
            verify(firebaseMessaging, times(1)).send(any(Message.class));
            verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
            Assertions.assertThat(content).isEqualTo("베님, 오늘 빨래하기 잊지 않으셨죠?");
        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Parameter_OneTargetReverseDto_경우 {

        @BeforeEach
        void setUp() {
            ve = Member.builder()
                .fcmToken("dummy")
                .nickname("베")
                .socialType(SocialType.APPLE)
                .role(Role.USER)
                .clientId("aaa")
                .name("aaa")
                .birthDay(LocalDate.now())
                .persona(1)
                .gender(Gender.MALE)
                .build();

            ro = Member.builder()
                .fcmToken("dummy")
                .nickname("로")
                .socialType(SocialType.APPLE)
                .role(Role.USER)
                .clientId("aaa")
                .name("aaa")
                .birthDay(LocalDate.now())
                .persona(1)
                .gender(Gender.MALE)
                .build();

            notificationType = NotificationType.COZY_MATE_REQUEST_TO;
        }

        @Test
        void testSendNotification() throws FirebaseMessagingException {
            // given
            OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(ve, ro,
                notificationType);
            NotificationContentDto notificationContentDto = NotificationContentDto.create(ve);
            String content = notificationType.generateContent(notificationContentDto);

            OneTargetReverseDto oneTargetReverseDto2 = OneTargetReverseDto.create(ro, ve,
                notificationType);
            NotificationContentDto notificationContentDto2 = NotificationContentDto.create(ro);
            NotificationType notificationType2 = NotificationType.COZY_MATE_REQUEST_FROM;
            String content2 = notificationType2.generateContent(notificationContentDto2);

            // when
            notificationService.sendNotification(oneTargetReverseDto);
            notificationService.sendNotification(oneTargetReverseDto2);

            // then
            verify(firebaseMessaging, times(2)).send(any(Message.class));
            verify(notificationLogRepository, times(2)).save(any(NotificationLog.class));
            Assertions.assertThat(content).isEqualTo("베님에게 코지메이트 신청을 보냈어요!");
            Assertions.assertThat(content2).isEqualTo("로님에게서 코지메이트 신청이 도착했어요!");
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Parameter_GroupTargetDto_경우 {

        @BeforeEach
        void setUp() {
            ve = Member.builder()
                .fcmToken("dummy")
                .nickname("베")
                .socialType(SocialType.APPLE)
                .role(Role.USER)
                .clientId("aaa")
                .name("aaa")
                .birthDay(LocalDate.now())
                .persona(1)
                .gender(Gender.MALE)
                .build();

            ro = Member.builder()
                .fcmToken("dummy")
                .nickname("로")
                .socialType(SocialType.APPLE)
                .role(Role.USER)
                .clientId("aaa")
                .name("aaa")
                .birthDay(LocalDate.now())
                .persona(1)
                .gender(Gender.MALE)
                .build();

            notificationType = NotificationType.ROOM_CREATED;
        }

        @Test
        void testSendNotification() throws FirebaseMessagingException {
            // given
            List<Member> memberList = List.of(ve, ro);
            GroupTargetDto groupTargetDto = GroupTargetDto.create(memberList,
                notificationType);

            NotificationContentDto notificationContentDto = NotificationContentDto.create(ve);
            String content = notificationType.generateContent(notificationContentDto);

            NotificationContentDto notificationContentDto2 = NotificationContentDto.create(ro);
            String content2 = notificationType.generateContent(notificationContentDto2);

            // when
            notificationService.sendNotification(groupTargetDto);

            // then
            verify(firebaseMessaging, times(memberList.size())).send(any(Message.class));
            verify(notificationLogRepository, times(memberList.size())).save(
                any(NotificationLog.class));
            Assertions.assertThat(content).isEqualTo(content2)
                .isEqualTo("방이 열렸어요, 얼른 가서 코지메이트를 만나봐요!");
        }
    }
}
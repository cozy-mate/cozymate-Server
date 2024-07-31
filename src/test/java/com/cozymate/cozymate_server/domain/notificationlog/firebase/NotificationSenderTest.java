package com.cozymate.cozymate_server.domain.notificationlog.firebase;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.GroupTargetVO;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.OneTargetReverseVO;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.OneTargetVO;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.TwoTargetVO;
import com.cozymate.cozymate_server.domain.notificationlog.service.NotificationLogCommandService;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.List;
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

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSender 클래스의")
class NotificationSenderTest {

    @Mock
    FirebaseMessaging firebaseMessaging;
    @Mock
    MemberRepository memberRepository;
    @Mock
    NotificationLogCommandService notificationLogCommandService;
    @InjectMocks
    NotificationSender notificationSender;

    OneTargetVO oneTargetVO;
    Member sender;
    Member recipient;
    OneTargetReverseVO oneTargetReverseVO;
    TwoTargetVO twoTargetVO;
    GroupTargetVO groupTargetVO;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class sendNotification_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class roomName이_null인_OneTargetVO의_경우 {

            @BeforeEach
            void setUp() throws FirebaseMessagingException {
                oneTargetVO = NotificationTestBuilder.testOneTargetVOWithRoomNameNullBuild();
                sender = NotificationTestBuilder.sender;

                given(memberRepository.findById(oneTargetVO.getMemberId())).willReturn(
                    Optional.of(sender));

                given(firebaseMessaging.send(any(Message.class))).willReturn(
                    "mock_return");

                doNothing().when(notificationLogCommandService)
                    .saveLog(any(NotificationLog.class));
            }

            @Test
            @DisplayName("알림 전송과 알림 로그 저장에 성공한다.")
            void it_returns_send_notification_save_notification_log()
                throws FirebaseMessagingException {
                NotificationLog result = notificationSender.sendNotification(oneTargetVO);
                then(memberRepository).should(timeout(1)).findById(oneTargetVO.getMemberId());
                then(firebaseMessaging).should(timeout(1)).send(any(Message.class));
                then(notificationLogCommandService).should(timeout(1))
                    .saveLog(any(NotificationLog.class));

                assertThat(result.getMember().getId())
                    .isEqualTo(oneTargetVO.getMemberId());
                assertThat(result.getCategory())
                    .isEqualTo(oneTargetVO.getNotificationType().getCategory());
                assertThat(result.getContent())
                    .isEqualTo(oneTargetVO.getNotificationType()
                        .generateContent(NotificationContentVO.create(sender)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class roomName이_포함된_OneTargetVO_경우 {

            @BeforeEach
            void setUp() throws FirebaseMessagingException {
                oneTargetVO = NotificationTestBuilder.testOneTargetVOWithRoomNameBuild();
                sender = NotificationTestBuilder.sender;

                given(memberRepository.findById(oneTargetVO.getMemberId())).willReturn(
                    Optional.of(sender));

                given(firebaseMessaging.send(any(Message.class))).willReturn(
                    "mock_return");

                doNothing().when(notificationLogCommandService)
                    .saveLog(any(NotificationLog.class));
            }

            @Test
            @DisplayName("알림 전송과 알림 로그 저장에 성공한다.")
            void it_returns_send_notification_save_notification_log()
                throws FirebaseMessagingException {
                NotificationLog result = notificationSender.sendNotification(oneTargetVO);

                assertThat(result.getMember())
                    .isEqualTo(sender);
                assertThat(result.getCategory())
                    .isEqualTo(oneTargetVO.getNotificationType().getCategory());
                assertThat(result.getContent())
                    .isEqualTo(oneTargetVO.getNotificationType()
                        .generateContent(NotificationContentVO.create(oneTargetVO.getRoomName())));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class OneTargetReverseVO의_경우 {

            @BeforeEach
            void setUp() throws FirebaseMessagingException {
                oneTargetReverseVO = NotificationTestBuilder.testOneTargetReverseVOBuild();
                sender = NotificationTestBuilder.sender;
                recipient = NotificationTestBuilder.recipient;

                given(memberRepository.findById(oneTargetReverseVO.getMyId())).willReturn(
                    Optional.of(sender));
                given(memberRepository.findById(oneTargetReverseVO.getRecipientId())).willReturn(
                    Optional.of(recipient));

                given(firebaseMessaging.send(any(Message.class))).willReturn(
                    "mock_return");

                doNothing().when(notificationLogCommandService)
                    .saveLog(any(NotificationLog.class));
            }

            @Test
            @DisplayName("알림 전송과 알림 로그 저장에 성공한다.")
            void it_returns_send_notification_save_notification_log() {
                NotificationLog result = notificationSender.sendNotification(
                    oneTargetReverseVO);

                assertThat(result.getMember()).isEqualTo(recipient);
                assertThat(result.getCategory()).isEqualTo(
                    oneTargetReverseVO.getNotificationType().getCategory());
                assertThat(result.getContent()).isEqualTo(oneTargetReverseVO.getNotificationType()
                    .generateContent(NotificationContentVO.create(sender)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class TwoTargetVO의_경우 {

            @BeforeEach
            void setUp() throws FirebaseMessagingException {
                twoTargetVO = NotificationTestBuilder.testTwoTargetVoBuild();
                sender = NotificationTestBuilder.sender;
                recipient = NotificationTestBuilder.recipient;

                given(memberRepository.findById(twoTargetVO.getMyId())).willReturn(
                    Optional.of(sender));
                given(memberRepository.findById(twoTargetVO.getRecipientId())).willReturn(
                    Optional.of(recipient));
                given(firebaseMessaging.send(any(Message.class))).willReturn(
                    "mock_return");
                doNothing().when(notificationLogCommandService)
                    .saveLog(any(NotificationLog.class));
            }

            @Test
            @DisplayName("알림 전송과 알림 로그 저장에 성공한다.")
            void it_returns_send_notification_save_notification_log() {
                List<NotificationLog> resultList = notificationSender.sendNotification(
                    twoTargetVO);

                assertThat(resultList.size()).isEqualTo(2);
                assertThat(resultList.get(0).getMember()).isEqualTo(recipient);
                assertThat(resultList.get(0).getCategory()).isEqualTo(
                    twoTargetVO.getRecipientNotificationType().getCategory());
                assertThat(resultList.get(0).getContent()).isEqualTo(
                    twoTargetVO.getRecipientNotificationType()
                        .generateContent(NotificationContentVO.create(sender)));

                assertThat(resultList.get(1).getMember()).isEqualTo(sender);
                assertThat(resultList.get(1).getCategory()).isEqualTo(
                    twoTargetVO.getMyNotificationType().getCategory());
                assertThat(resultList.get(1).getContent()).isEqualTo(
                    twoTargetVO.getMyNotificationType()
                        .generateContent(NotificationContentVO.create(recipient)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class GroupTargetVo의_경우 {

            @BeforeEach
            void setUp() throws FirebaseMessagingException {
                groupTargetVO = NotificationTestBuilder.testGroupTargetVOBuild();
                List<Long> memberIdList = groupTargetVO.getMemberIdList();
                sender = NotificationTestBuilder.sender;
                recipient = NotificationTestBuilder.recipient;

                given(memberRepository.findById(memberIdList.get(0))).willReturn(
                    Optional.of(sender));
                given(memberRepository.findById(memberIdList.get(1))).willReturn(
                    Optional.of(recipient));

                given(firebaseMessaging.send(any(Message.class))).willReturn(
                    "mock_return");
                doNothing().when(notificationLogCommandService)
                    .saveLog(any(NotificationLog.class));
            }

            @Test
            @DisplayName("알림 전송과 알림 로그 저장에 성공한다.")
            void it_returns_send_notification_save_notification_log() {
                List<NotificationLog> resultList = notificationSender.sendNotification(
                    groupTargetVO);

                assertThat(resultList.size()).isEqualTo(2);
                assertThat(resultList.get(0).getMember()).isEqualTo(sender);
                assertThat(resultList.get(0).getCategory()).isEqualTo(
                    groupTargetVO.getNotificationType().getCategory());
                assertThat(resultList.get(0).getContent()).isEqualTo(
                    groupTargetVO.getNotificationType()
                        .generateContent(NotificationContentVO.create(sender)));

                assertThat(resultList.get(1).getMember()).isEqualTo(recipient);
                assertThat(resultList.get(1).getCategory()).isEqualTo(
                    groupTargetVO.getNotificationType().getCategory());
                assertThat(resultList.get(1).getContent()).isEqualTo(
                    groupTargetVO.getNotificationType()
                        .generateContent(NotificationContentVO.create(recipient)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 유효하지_않은_memberId인_경우 {

            @BeforeEach
            void setUp() {
                oneTargetVO = NotificationTestBuilder.testOneTargetVOWithRoomNameNullBuild();

                given(memberRepository.findById(oneTargetVO.getMemberId())).willReturn(
                    Optional.empty());
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_not_found_member_exception() {
                assertThatThrownBy(() -> notificationSender.sendNotification(oneTargetVO))
                    .isInstanceOf(GeneralException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 알림_전송에_실패하면 {

            @BeforeEach
            void setUp() throws FirebaseMessagingException {
                oneTargetVO = NotificationTestBuilder.testOneTargetVOWithRoomNameNullBuild();
                sender = NotificationTestBuilder.sender;

                given(memberRepository.findById(oneTargetVO.getMemberId())).willReturn(
                    Optional.of(sender));
                FirebaseMessagingException firebaseException = mock(FirebaseMessagingException.class);
                given(firebaseMessaging.send(any(Message.class))).willThrow(firebaseException);
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_notification_fail_exception() {
                assertThatThrownBy(() -> notificationSender.sendNotification(oneTargetVO))
                    .isInstanceOf(GeneralException.class);
            }
        }
    }
}